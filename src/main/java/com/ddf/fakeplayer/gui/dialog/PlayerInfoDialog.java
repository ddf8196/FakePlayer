package com.ddf.fakeplayer.gui.dialog;

import com.ddf.fakeplayer.util.Config;
import com.ddf.fakeplayer.gui.GUIMain;

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
        JPanel buttonBar = new JPanel();
        ok = new JButton("确定");
        cancel = new JButton("取消");

        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setLayout(new BorderLayout());
        setContentPane(content);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0, 0, 0};
        gridBagLayout.rowHeights = new int[] {0, 0, 0};
        gridBagLayout.columnWeights = new double[] {0.0, 0.0, 1.0E-4};
        gridBagLayout.rowWeights = new double[] {0.0, 0.0, 1.0E-4};
        playerInfoPanel.setLayout(gridBagLayout);
        playerInfoPanel.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));
        playerInfoPanel.add(name, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));
        playerInfoPanel.add(skinLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        playerInfoPanel.add(skin, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        content.add(playerInfoPanel, BorderLayout.CENTER);
        buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
        gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {85, 80};
        buttonBar.setLayout(gridBagLayout);
        buttonBar.add(ok, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        buttonBar.add(cancel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        content.add(buttonBar, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void initData() {
        switch (type) {
            case TYPE_ADD:
                setTitle("添加假人");
                break;
            case TYPE_EDIT:
                setTitle("编辑假人");
                Config.PlayerData data =  main.getConfig().getPlayerData(playerName);
                name.setText(data.getName());
                skin.setSelectedItem(data.getSkin());
                break;
        }
        name.addActionListener(e -> {
            addOrEdit();
        });
        ok.addActionListener(e -> {
            addOrEdit();
        });
        cancel.addActionListener(e -> {
            dispose();
        });
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
                main.addPlayer(playerName, skin);
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
                main.addPlayer(text, skin.getSelectedItem().toString());
                break;
            }
        }
        dispose();
    }
}
