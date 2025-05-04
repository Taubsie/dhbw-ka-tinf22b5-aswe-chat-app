package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.chat.Chat;
import de.dhbw.ka.tinf22b5.chat.Message;
import de.dhbw.ka.tinf22b5.chat.User;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.configuration.FileConfigurationRepository;
import de.dhbw.ka.tinf22b5.net.broadcast.UDPBroadcastUtil;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.RawReceivedBroadcastPacket;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.SendingBroadcastPacket;
import de.dhbw.ka.tinf22b5.net.p2p.TCPP2PUtil;
import de.dhbw.ka.tinf22b5.net.p2p.packets.ChatRelatedJsonP2PPacket;
import de.dhbw.ka.tinf22b5.net.p2p.packets.MessageSendP2PPacket;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.components.*;
import de.dhbw.ka.tinf22b5.terminal.render.layout.ListLayout;
import de.dhbw.ka.tinf22b5.terminal.render.layout.VerticalSplitLayout;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class ChatDialog extends Dialog {

    private final ListRenderable<ConstSingleLineStringRenderable> userList;
    private final ListRenderable<BorderRenderable> chatList;
    private final TextInputRenderable textInput;

    private final ConfigurationRepository configurationRepository;
    private final TCPP2PUtil tcpp2PUtil;
    private final UDPBroadcastUtil udpBroadcastUtil;

    private final List<Chat> chats;
    int currentChatId = -1;

    private Interactable currentlyFocused;

    public ChatDialog() {
        this.configurationRepository = new FileConfigurationRepository();
        this.tcpp2PUtil = new TCPP2PUtil(configurationRepository);
        this.udpBroadcastUtil = new UDPBroadcastUtil(configurationRepository);

        this.layoutManager = new VerticalSplitLayout(0.3f);

        this.userList = new ListRenderable<>();
        this.addComponent(userList);

        this.chatList = new ListRenderable<>();
        this.textInput = new TextInputRenderable("");
        this.textInput.setFocus(true);
        this.currentlyFocused = this.textInput;

        EmptyPanel panel = new EmptyPanel(new ListLayout(false));
        panel.addComponent(chatList);
        panel.addComponent(new BorderRenderable(textInput, BorderRenderable.BorderStyle.DASHED, 1,
                BorderRenderable.BORDER_TOP | BorderRenderable.BORDER_BOTTOM | BorderRenderable.BORDER_LEFT | BorderRenderable.BORDER_RIGHT));
        this.addComponent(new BorderRenderable(panel, BorderRenderable.BorderStyle.DASHED, 1, BorderRenderable.BORDER_LEFT));

        chats = new ArrayList<>();

        //TODO add listener for tcp ping response
        //TODO (add listener for udp received)

        //TODO send udp ping

        tcpp2PUtil.addP2PListener(packet -> {
            if(packet instanceof MessageSendP2PPacket messagePacket) {
                Message message = messagePacket.fromJson();

                Optional<Chat> chat = chats.stream().filter(it -> it.getSender().getName().equals(message.getSender().getName())).findFirst();

                chat.ifPresent(value -> value.getMessages().add(message));
            }
        });

        udpBroadcastUtil.addBroadcastListener(packet -> {
            if(packet instanceof RawReceivedBroadcastPacket) {
                chats.add(new Chat(new User(new String(packet.getData(), StandardCharsets.UTF_8))));
            }
        });

        udpBroadcastUtil.sendBroadcastPacket(() -> configurationRepository.getConfigurationValue(ConfigurationKey.USERNAME).map(it -> it.getBytes(StandardCharsets.UTF_8)).orElseThrow(() -> new RuntimeException("Please select a username first.")));

        updateChatUI();
    }

    public void updateChatUI() {
        int userSelectedIdx = userList.getSelectedIdx();

        userList.clearItems();
        userList.setSelectedIdx(userSelectedIdx);

        for (Chat chat : chats) {
            userList.addItem(new ConstSingleLineStringRenderable(chat.getSender().getName()));
        }


        if(userList.getItemCount() <= 0 || userList.getSelectedIdx() < 0 || userList.getSelectedIdx() >= userList.getItemCount())
            return;

        int chatSelectedIdx = chatList.getSelectedIdx();
        int chatLength = chatList.getItemCount();

        Chat chat = chats.get(userList.getSelectedIdx());
        chatList.clearItems();
        for (int i = 0; i < chat.getMessages().size(); i++) {
            Message message = chat.getMessages().get(i);
            int borderModifiers = message.isRemoteMessage() ? BorderRenderable.BORDER_RIGHT : BorderRenderable.BORDER_LEFT;
            borderModifiers |= BorderRenderable.BORDER_BOTTOM;

            String str = message.isRemoteMessage() ? chat.getSender().getName() : "Me";
            str += ": " + message.getMessage();

            chatList.addItem(new BorderRenderable(new ConstSingleLineStringRenderable(str), BorderRenderable.BorderStyle.DASHED, 1, borderModifiers));
        }

        if(currentChatId == userSelectedIdx)
            chatList.setSelectedIdx(chatList.getItemCount() - chatLength + chatSelectedIdx);
        else {
            currentChatId = chatSelectedIdx;
            chatList.setSelectedIdx(0);
        }
    }

    @Override
    public boolean handleInput(TerminalHandler terminal, TerminalKeyEvent event) {
        if (currentlyFocused != null && currentlyFocused.handleInput(event))
            return true;

        switch (event.getTerminalKey()) {
            case TerminalKey.TK_TAB:
                doCyclicFocusSwitch();
                return true;
            case TerminalKey.TK_ENTER:
                if (currentlyFocused == textInput) {
                    sendMessage(terminal);
                    return true;
                } else if (currentlyFocused == chatList) {
                    // TODO: currently not implemented
                } else if (currentlyFocused == userList) {
                    updateChatUI();
                }

        }

        return false;
    }

    private void sendMessage(TerminalHandler handler) {

        if (textInput.getText().isBlank())
            return;

        if(chats.isEmpty())
            return;

        // TODO: Networking
        chats.get(userList.getSelectedIdx()).getMessages().add(0, new Message(new User("Me"), textInput.getText(), Calendar.getInstance(), false));

        textInput.clearText();
        updateChatUI();
        try {
            handler.updateTerminal();
        } catch (IOException e) {
            // ignored
        }
    }

    private void doCyclicFocusSwitch() {
        currentlyFocused.setFocus(false);
        if(currentlyFocused == userList)
            currentlyFocused = chatList;
        else if(currentlyFocused == chatList)
            currentlyFocused = textInput;
        else if(currentlyFocused == textInput)
            currentlyFocused = userList;
        currentlyFocused.setFocus(true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0, 0);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }
}
