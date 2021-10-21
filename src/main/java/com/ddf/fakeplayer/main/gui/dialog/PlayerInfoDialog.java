package com.ddf.fakeplayer.main.gui.dialog;

import com.ddf.fakeplayer.client.Client;
import com.ddf.fakeplayer.main.I18N;
import com.ddf.fakeplayer.main.config.PlayerData;
import com.ddf.fakeplayer.main.gui.GUIMain;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerInfoDialog extends JDialog {
    public static final int TYPE_ADD = 0;
    public static final int TYPE_EDIT = 1;

    private final GUIMain main;
    private final int type;
    private final String playerName;
    private final String playerSkin;

    private JTextField name;
    private JList<String> scripts;
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
        if (type != TYPE_ADD) {
            this.playerSkin = main.getConfig().getPlayerData(playerName).getSkin();
        } else {
            this.playerSkin = null;
        }
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
        allowChatMessageControl = new JCheckBox(I18N.get("checkBox.allowChatMessageControl"));
        JPanel buttonBar = new JPanel();
        ok = new JButton(I18N.get("button.ok"));
        cancel = new JButton(I18N.get("button.cancel"));

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
                setTitle(I18N.get("title.addFakePlayer"));
                break;
            case TYPE_EDIT:
                setTitle(I18N.get("title.editFakePlayer"));
                PlayerData data = main.getConfig().getPlayerData(playerName);
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
        String playerName = name.getText();
        String skin = this.skin.getSelectedItem().toString();
        boolean allowChatMessageControl = this.allowChatMessageControl.isSelected();
        switch (type) {
            case TYPE_ADD: {
                if (playerName == null || playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(main.getFrame(), "名称不能为空");
                    return;
                }
                if (main.getClient(playerName) != null) {
                    JOptionPane.showMessageDialog(main.getFrame(), "假人已存在");
                    return;
                }
                PlayerData playerData = new PlayerData(playerName, skin, allowChatMessageControl);
                main.addPlayer(playerData);
                dispose();
                break;
            }
            case TYPE_EDIT: {
                if (playerName == null || playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(main.getFrame(), "名称不能为空");
                    return;
                }
                if (!playerName.equals(this.playerName) && main.getClient(playerName) != null) {
                    JOptionPane.showMessageDialog(main.getFrame(), "假人已存在");
                    return;
                }
                if (playerName.equals(this.playerName) && skin.equals(playerSkin)) {
                    Client client = main.getClient(playerName);
                    client.setAllowChatMessageControl(allowChatMessageControl);
                } else {
                    PlayerData playerData = new PlayerData(playerName, skin, allowChatMessageControl);
                    main.removePlayer(this.playerName);
                    main.addPlayer(playerData);
                }
                break;
            }
        }
        dispose();
    }
}
