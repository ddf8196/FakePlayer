package com.ddf.fakeplayer.main.gui.dialog;

import com.ddf.fakeplayer.client.Client;
import com.ddf.fakeplayer.main.I18N;
import com.ddf.fakeplayer.main.config.CustomSkinData;
import com.ddf.fakeplayer.main.config.PlayerData;
import com.ddf.fakeplayer.main.gui.GUIMain;
import com.ddf.fakeplayer.util.Image;
import com.ddf.fakeplayer.util.SkinUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

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

    private SkinComboBoxModel skinComboBoxModel;

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
        initListener();
        initData();
    }

    private void initLayout() {
        JPanel content = new JPanel();
        JPanel playerInfoPanel = new JPanel();
        JLabel nameLabel = new JLabel(I18N.get("guiMain.label.playerName"));
        name = new JTextField();
        JLabel skinLabel = new JLabel(I18N.get("guiMain.label.playerSkin"));
        skin = new JComboBox<>();
        allowChatMessageControl = new JCheckBox(I18N.get("guiMain.checkBox.allowChatMessageControl"));
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

    private void initListener() {
        skin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JFileChooser fileChooser = new JFileChooser();

                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setAcceptAllFileFilterUsed(false);
                    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(I18N.get("playerInfoDialog.fileFilter.description.skinFile"), "png"));
                    int result = fileChooser.showOpenDialog(PlayerInfoDialog.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        Image image = new Image();
                        image.load(file);
//                        String name = JOptionPane.showInputDialog(PlayerInfoDialog.this, I18N.get(""));
                        String name = file.getName();
                        CustomSkinData skinData = new CustomSkinData();
                        skinData.setImageWidth(image.getWidth());
                        skinData.setImageHeight(image.getHeight());
                        skinData.setImageData(SkinUtil.encodeSkinToBase64(image));
                        main.getConfig().addCustomSkin(name, skinData);
                        skinComboBoxModel.add(name);
                        skin.setSelectedItem(name);
                    }
                }
            }
        });
    }

    private void initData() {
        skinComboBoxModel = new SkinComboBoxModel();
        skin.setModel(skinComboBoxModel);
        pack();
        switch (type) {
            case TYPE_ADD:
                setTitle(I18N.get("title.addFakePlayer"));
                skin.setSelectedItem("steve");
                break;
            case TYPE_EDIT:
                setTitle(I18N.get("title.editFakePlayer"));
                PlayerData data = main.getConfig().getPlayerData(playerName);
                name.setText(data.getName());
                String skinType = data.getSkin();
                if ("steve".equals(skinType) || "alex".equals(skinType))
                    skin.setSelectedItem(skinType);
                else
                    skin.setSelectedItem(data.getCustomSkin());
                allowChatMessageControl.setSelected(data.isAllowChatMessageControl());
                break;
        }
        name.addActionListener(e -> addOrEdit());
        ok.addActionListener(e -> addOrEdit());
        cancel.addActionListener(e -> dispose());
    }

    private void addOrEdit() {
        String playerName = name.getText();
        String skin = Objects.toString(this.skin.getSelectedItem());
        boolean allowChatMessageControl = this.allowChatMessageControl.isSelected();
        switch (type) {
            case TYPE_ADD: {
                if (playerName == null || playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(main.getFrame(), I18N.get("playerInfoDialog.message.emptyPlayerName"));
                    return;
                }
                if (main.getClient(playerName) != null) {
                    JOptionPane.showMessageDialog(main.getFrame(), I18N.get("playerInfoDialog.message.playerAlreadyExists"));
                    return;
                }
                PlayerData playerData = new PlayerData(playerName, skin, allowChatMessageControl);
                if (!"steve".equals(skin) && !"alex".equals(skin)) {
                    playerData.setSkin("custom");
                    playerData.setCustomSkin(skin);
                }
                main.addPlayer(playerData);
                dispose();
                break;
            }
            case TYPE_EDIT: {
                if (playerName == null || playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(main.getFrame(), I18N.get("playerInfoDialog.message.emptyPlayerName"));
                    return;
                }
                if (!playerName.equals(this.playerName) && main.getClient(playerName) != null) {
                    JOptionPane.showMessageDialog(main.getFrame(), I18N.get("playerInfoDialog.message.playerAlreadyExists"));
                    return;
                }
                if (playerName.equals(this.playerName) && skin.equals(playerSkin)) {
                    Client client = main.getClient(playerName);
                    client.setAllowChatMessageControl(allowChatMessageControl);
                } else {
                    PlayerData playerData = new PlayerData(playerName, skin, allowChatMessageControl);
                    if (!"steve".equals(skin) && !"alex".equals(skin)) {
                        playerData.setSkin("custom");
                        playerData.setCustomSkin(skin);
                    }
                    main.removePlayer(this.playerName);
                    main.addPlayer(playerData);
                }
                break;
            }
        }
        dispose();
    }

    private class SkinComboBoxModel extends AbstractListModel<String> implements ComboBoxModel<String> {
        private String selectedItem;
        private ArrayList<String> strings = new ArrayList<>();

        SkinComboBoxModel() {
            strings.add("steve");
            strings.add("alex");
            strings.addAll(main.getConfig().getCustomSkins().keySet());
        }

        public void add(String name) {
            strings.add(name);
            fireIntervalAdded(this, strings.size() - 2, strings.size() - 1);
        }

        @Override
        public void setSelectedItem(Object anItem) {
            selectedItem = Objects.toString(anItem);
        }

        @Override
        public String getSelectedItem() {
            return selectedItem;
        }

        @Override
        public int getSize() {
            return strings.size();
        }

        @Override
        public String getElementAt(int index) {
            return strings.get(index);
        }
    }
}
