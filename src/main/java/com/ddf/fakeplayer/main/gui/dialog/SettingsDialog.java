package com.ddf.fakeplayer.main.gui.dialog;

import com.ddf.fakeplayer.main.I18N;
import com.ddf.fakeplayer.main.gui.GUIMain;

import javax.swing.*;

public class SettingsDialog extends JDialog {
    private final GUIMain main;
    private JPanel content;

    public SettingsDialog(GUIMain main) {
        super(main.getFrame());
        this.main = main;
        initLayout();
        initListener();
        initData();
    }

    private void initLayout() {
        setTitle(I18N.get("title.settings"));
    }

    private void initListener() {

    }

    private void initData() {

    }
}
