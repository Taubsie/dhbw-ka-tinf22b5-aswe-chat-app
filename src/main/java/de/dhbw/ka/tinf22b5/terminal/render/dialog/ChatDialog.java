package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.components.*;
import de.dhbw.ka.tinf22b5.terminal.render.layout.ListLayout;
import de.dhbw.ka.tinf22b5.terminal.render.layout.VerticalSplitLayout;

import java.awt.*;

public class ChatDialog extends Dialog {

    private final ListRenderable<ConstSingleLineStringRenderable> userList;
    private final ListRenderable<ConstSingleLineStringRenderable> chatList;
    private final TextInputRenderable textInput;

    public ChatDialog() {
        this.layoutManager = new VerticalSplitLayout(0.3f);

        this.userList = new ListRenderable<>();
        this.addComponent(userList);

        this.chatList = new ListRenderable<>();
        this.textInput = new TextInputRenderable("");
        this.textInput.setFocus(true);

        EmptyPanel panel = new EmptyPanel(new ListLayout(false));
        panel.addComponent(chatList);
        panel.addComponent(new BorderRenderable(textInput, BorderRenderable.BorderStyle.DASHED, 1,
                BorderRenderable.BORDER_TOP | BorderRenderable.BORDER_BOTTOM | BorderRenderable.BORDER_LEFT | BorderRenderable.BORDER_RIGHT));
        this.addComponent(new BorderRenderable(panel, BorderRenderable.BorderStyle.DASHED, 1, BorderRenderable.BORDER_LEFT));

        userList.addItem(new ConstSingleLineStringRenderable("User1"));
        userList.addItem(new ConstSingleLineStringRenderable("User2"));

        chatList.addItem(new ConstSingleLineStringRenderable("Message 1"));
        chatList.addItem(new ConstSingleLineStringRenderable("Message 2"));
    }

    @Override
    public boolean handleInput(TerminalHandler terminal, TerminalKeyEvent event) {
        return false;
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
