package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.chat.Chat;
import de.dhbw.ka.tinf22b5.chat.Message;
import de.dhbw.ka.tinf22b5.chat.User;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.configuration.FileConfigurationRepository;
import de.dhbw.ka.tinf22b5.net.broadcast.UDPBroadcastUtil;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.ReceivingWelcomePacket;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.SendingWelcomePacket;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.data.WelcomeData;
import de.dhbw.ka.tinf22b5.net.p2p.TCPP2PUtil;
import de.dhbw.ka.tinf22b5.net.p2p.packets.MessageSendP2PPacket;
import de.dhbw.ka.tinf22b5.net.p2p.packets.WelcomeP2PPacket;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.components.*;
import de.dhbw.ka.tinf22b5.terminal.render.layout.ListLayout;
import de.dhbw.ka.tinf22b5.terminal.render.layout.VerticalSplitLayout;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.List;

public class ChatDialog extends Dialog {

    private final TerminalHandler terminalHandler;

    private final ListRenderable<ConstSingleLineStringRenderable> userList;
    private final ListRenderable<BorderRenderable> chatList;
    private final TextInputRenderable textInput;

    private final ConfigurationRepository configurationRepository;
    private final TCPP2PUtil tcpp2PUtil;
    private final UDPBroadcastUtil udpBroadcastUtil;

    private final Map<User, SocketAddress> userAddress = new HashMap<>();

    private final List<Chat> chats;
    int currentChatId = -1;

    private final User myself;

    private Interactable currentlyFocused;

    public ChatDialog(TerminalHandler terminalHandler) {
        this.terminalHandler = terminalHandler;

        this.configurationRepository = new FileConfigurationRepository();
        this.tcpp2PUtil = new TCPP2PUtil(configurationRepository);
        tcpp2PUtil.attachShutdownHook();
        tcpp2PUtil.open();

        this.udpBroadcastUtil = new UDPBroadcastUtil(configurationRepository);
        udpBroadcastUtil.attachShutdownHook();
        udpBroadcastUtil.open();

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

        myself = new User(configurationRepository.getConfigurationValue(ConfigurationKey.USERNAME).orElseThrow(() -> new RuntimeException("Please select a username first.")));

        chats = new ArrayList<>();

        //TODO load chats from storage?

        tcpp2PUtil.addP2PListener(packet -> {
            if(packet instanceof MessageSendP2PPacket messagePacket) {
                Message message = messagePacket.getJsonData();

                Optional<Chat> chat = chats.stream().filter(it -> it.getSender().getName().equals(message.getSender().getName())).findFirst();

                chat.ifPresent(value -> value.getMessages().addFirst(message));

                updateTerminal();
            }

            if(packet instanceof WelcomeP2PPacket welcomePacket) {
                //TODO do sth with the messages list?
                Chat chat = welcomePacket.getJsonData();

                User user = chat.getSender();

                SocketAddress address = packet.getRemoteAddress();

                userAddress.put(user, address);

                addChat(user);

                updateTerminal();
            }
        });

        udpBroadcastUtil.addBroadcastListener(packet -> {
            if(packet instanceof ReceivingWelcomePacket welcomePacket) {
                User user = new User(welcomePacket.getWelcomeData().getUsername());

                SocketAddress address = new InetSocketAddress(packet.getRemoteAddress(), welcomePacket.getWelcomeData().getPort());

                userAddress.put(user, address);

                addChat(user);

                updateTerminal();

                tcpp2PUtil.sendP2PPacket(new WelcomeP2PPacket(new Chat(myself), address));
            }
        });

        udpBroadcastUtil.sendBroadcastPacket(
                new SendingWelcomePacket(new WelcomeData(configurationRepository.getConfigurationValue(ConfigurationKey.USERNAME).orElseThrow(() -> new RuntimeException("Please select a username first.")), tcpp2PUtil.getServerPort()))
        );

        updateChatUI();
    }

    private void addChat(User user) {
        if(chats.stream().anyMatch(it -> it.getSender().equals(user)))
            return;

        chats.add(new Chat(user));
    }

    private void updateTerminal() {
        updateChatUI();

        try {
            terminalHandler.updateTerminal();
        } catch (IOException ignored) {

        }
    }

    private void updateChatUI() {
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

        Chat chat = chats.get(userSelectedIdx);
        chatList.clearItems();
        for (int i = 0; i < chat.getMessages().size(); i++) {
            Message message = chat.getMessages().get(i);
            int borderModifiers = message.isRemoteMessage() ? BorderRenderable.BORDER_RIGHT : BorderRenderable.BORDER_LEFT;
            borderModifiers |= BorderRenderable.BORDER_BOTTOM;

            String str = message.isRemoteMessage() ? chat.getSender().getName() : "Me";
            if (str.length() > 20)
                str = str.substring(0, 20);

            str += ": " + message.getMessage();

            chatList.addItem(new BorderRenderable(new ConstSingleLineStringRenderable(str), BorderRenderable.BorderStyle.DASHED, 1, borderModifiers));
        }

        if(currentChatId == userSelectedIdx)
            chatList.setSelectedIdx(chatList.getItemCount() - chatLength + chatSelectedIdx);
        else {
            currentChatId = userSelectedIdx;
            chatList.setSelectedIdx(0);
        }
    }

    @Override
    public void layout() {
        updateChatUI();
        super.layout();
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
                    // TODO: nothing to do no enter needed
                }

        }

        return false;
    }

    private void sendMessage(TerminalHandler handler) {

        if (textInput.getText().isBlank())
            return;

        if(chats.isEmpty())
            return;

        Message displayMessage = new Message(new User("Me"), textInput.getText(), Calendar.getInstance(), false);

        chats.get(userList.getSelectedIdx()).getMessages().addFirst(displayMessage);

        Optional<SocketAddress> address = userAddress.entrySet().stream().filter(it -> it.getKey().equals(chats.get(userList.getSelectedIdx()).getSender())).findFirst().map(Map.Entry::getValue);

        Message sendMessage = new Message(myself, textInput.getText(), Calendar.getInstance(), true);

        address.ifPresent(socketAddress -> tcpp2PUtil.sendP2PPacket(new MessageSendP2PPacket(sendMessage, socketAddress)));

        textInput.clearText();
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
