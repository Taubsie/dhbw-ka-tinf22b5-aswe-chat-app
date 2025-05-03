package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.chat.Chat;
import de.dhbw.ka.tinf22b5.chat.Message;
import de.dhbw.ka.tinf22b5.chat.User;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.components.*;
import de.dhbw.ka.tinf22b5.terminal.render.layout.ListLayout;
import de.dhbw.ka.tinf22b5.terminal.render.layout.VerticalSplitLayout;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatDialog extends Dialog {

    private final ListRenderable<ConstSingleLineStringRenderable> userList;
    private final ListRenderable<BorderRenderable> chatList;
    private final TextInputRenderable textInput;

    private final List<Chat> chats;
    int currentChatId = -1;

    private Interactable currentlyFocused;

    public ChatDialog() {
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

        // TODO: create chats with networking or create setter
        chats = new ArrayList<>();
        Chat c1 = new Chat(new User("Test1"));
        chats.add(c1);
        c1.addMessage(new Message("Message1", Calendar.getInstance(), true));
        c1.addMessage(new Message("Message2", Calendar.getInstance(), false));
        c1.addMessage(new Message("Message3", Calendar.getInstance(), false));
        c1.addMessage(new Message("Message4", Calendar.getInstance(), true));

        Chat c2 = new Chat(new User("Test2"));
        chats.add(c2);
        c2.addMessage(new Message("Message1", Calendar.getInstance(), false));
        c2.addMessage(new Message("Message2", Calendar.getInstance(), false));
        c2.addMessage(new Message("Message3", Calendar.getInstance(), false));
        c2.addMessage(new Message("Message4", Calendar.getInstance(), true));
    }

    private void updateChatUI() {
        int userSelectedIdx = userList.getSelectedIdx();

        userList.clearItems();
        userList.setSelectedIdx(userSelectedIdx);

        for (Chat chat : chats) {
            userList.addItem(new ConstSingleLineStringRenderable(chat.getRemoteUser().getName()));
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

            String str = message.isRemoteMessage() ? chat.getRemoteUser().getName() : "Me";
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

        // TODO: Networking
        chats.get(userList.getSelectedIdx()).addMessage(new Message(textInput.getText(), Calendar.getInstance(), false));

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
