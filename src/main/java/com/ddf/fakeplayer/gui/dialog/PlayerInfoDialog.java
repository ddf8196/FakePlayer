package com.ddf.fakeplayer.gui.dialog;

import com.ddf.fakeplayer.util.Config;
import com.ddf.fakeplayer.gui.GUIMain;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerInfoDialog extends JDialog {
    public static final int TYPE_ADD = 0;
    public static final int TYPE_EDIT = 1;

    private final GUIMain main;
    private final int type;
    private String playerName;

    private JTextField name;
    private JComboBox<String> skin;
    private JCheckBox allowChatMessageControl;
    private JButton ok;
    private JButton cancel;

    public PlayerInfoDialog(GUIMain main) {
        this(main, TYPE_ADD, null);
    }

    public PlayerInfoDialog(GUIMain main, String playerName) {
        this(main, TYPE_EDIT, playerName);
    }

    public PlayerInfoDialog(GUIMain main, int type, String playerName) {
        super(main.getFrame());
        this.main = main;
        this.type = type;
        this.playerName = playerName;
        initLayout();
        initData();
    }

    private void initLayout() {
        JPanel content = new JPanel();
        JPanel playerInfoPanel = new JPanel();
        JLabel nameLabel = new JLabel("假人名称");
        name = new JTextField();
        JLabel skinLabel = new JLabel("皮肤");
        skin = new JComboBox<>(new String[]{
                "steve",
                "alex"
        });
        allowChatMessageControl = new JCheckBox("允许聊天信息控制");
        JPanel buttonBar = new JPanel();
        ok = new JButton("确定");
        cancel = new JButton("取消");

        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setLayout(new BorderLayout());
        setContentPane(content);
        playerInfoPanel.setLayout(new MigLayout("insets 0,hidemode 3", "[fill][grow,fill]", "[][][]"));
        playerInfoPanel.add(nameLabel, "cell 0 0");
        playerInfoPanel.add(name, "cell 1 0");
        playerInfoPanel.add(skinLabel, "cell 0 1");
        playerInfoPanel.add(skin, "cell 1 1");
        playerInfoPanel.add(allowChatMessageControl, "cell 0 2 2 1");
        content.add(playerInfoPanel, BorderLayout.CENTER);
        buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
        buttonBar.setLayout(new MigLayout("insets 0,hidemode 3,gap 5 5", "[80,grow,fill][80,grow,fill]", "[fill]"));
        buttonBar.add(ok, "cell 0 0");
        buttonBar.add(cancel, "cell 1 0");
        content.add(buttonBar, BorderLayout.SOUTH);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(getOwner());
    }

    private void initData() {
        switch (type) {
            case TYPE_ADD:
                setTitle("添加假人");
                break;
            case TYPE_EDIT:
                setTitle("编辑假人");
                Config.PlayerData data = main.getConfig().getPlayerData(playerName);
                name.setText(data.getName());
                skin.setSelectedItem(data.getSkin());
                allowChatMessageControl.setSelected(data.isAllowChatMessageControl());
                break;
        }
        name.addActionListener(e -> addOrEdit());
        ok.addActionListener(e -> addOrEdit());
        cancel.addActionListener(e -> dispose());
    }

    private void addOrEdit() {
        switch (type) {
            case TYPE_ADD: {
                String playerName = name.getText();
                String skin = this.skin.getSelectedItem().toString();
                if (playerName == null || playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(main.getFrame(), "名称不能为空");
                    return;
                }
                if (main.getClient(playerName) != null) {
                    JOptionPane.showMessageDialog(main.getFrame(), "假人已存在");
                    return;
                }
                main.addPlayer(playerName, skin, allowChatMessageControl.isSelected());
                dispose();
                break;
            }
            case TYPE_EDIT: {
                String text = name.getText();
                if (text == null || text.isEmpty()) {
                    JOptionPane.showMessageDialog(main.getFrame(), "名称不能为空");
                    return;
                }
                if (!text.equals(playerName) && main.getClient(text) != null) {
                    JOptionPane.showMessageDialog(main.getFrame(), "假人已存在");
                    return;
                }
                main.removePlayer(playerName);
                main.addPlayer(text, skin.getSelectedItem().toString(), allowChatMessageControl.isSelected());
                break;
            }
        }
        dispose();
    }
}
