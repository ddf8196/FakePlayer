package com.ddf.fakeplayer.main.gui.dialog;

import com.ddf.fakeplayer.main.gui.GUIMain;
import com.ddf.fakeplayer.util.SimpleProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;

public class PublicKeyDialog extends JDialog {
    private final GUIMain main;
    private JPanel content;
    private JTextArea publicKey;
    private JButton add;
    private JButton ok;

    public PublicKeyDialog(GUIMain main) {
        super(main.getFrame());
        this.main = main;
        initLayout();
        initData();
    }

    private void initLayout() {
        content = new JPanel();
        JPanel publicKeyPanel = new JPanel();
        JLabel publicKeyLabel = new JLabel("服务器公钥:");
        JScrollPane publicKeyScrollPane = new JScrollPane();
        publicKey = new JTextArea();
        add = new JButton("将公钥添加至server.properties");
        JPanel spacer = new JPanel();
        JPanel buttonBar = new JPanel();
        ok = new JButton("确定");

        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setLayout(new BorderLayout());
        setContentPane(content);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0, 0, 0};
        gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0001};
        gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0001};
        publicKeyPanel.setLayout(gridBagLayout);
        publicKeyPanel.add(publicKeyLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));
        publicKey.setLineWrap(true);
        publicKey.setColumns(18);
        publicKey.setRows(7);
        publicKey.setEditable(false);
        publicKeyScrollPane.setViewportView(publicKey);
        publicKeyPanel.add(publicKeyScrollPane, new GridBagConstraints(1, 0, 1, 4, 0.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));
        publicKeyPanel.add(add, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));
        spacer.setLayout(new BorderLayout());
        publicKeyPanel.add(spacer, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        content.add(publicKeyPanel, BorderLayout.CENTER);
        buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
        gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {80};
        gridBagLayout.columnWeights = new double[] {1.0};
        buttonBar.setLayout(gridBagLayout);
        buttonBar.add(ok, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        content.add(buttonBar, BorderLayout.SOUTH);
        setTitle("服务器公钥");
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(getOwner());
    }

    private void initData() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                content.requestFocus();
            }
        });
        publicKey.setText(main.getConfig().getServerPublicKey());
        add.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("server.properties", "properties"));
            int result = fileChooser.showOpenDialog(main.getFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                Path path = fileChooser.getSelectedFile().toPath();
                SimpleProperties properties = new SimpleProperties();
                try {
                    properties.load(path);
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(this, "配置文件加载失败: " + exception.toString());
                    return;
                }
                properties.putProperty("trusted-key", main.getConfig().getServerPublicKey());
                try {
                    properties.save(path);
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(this, "配置文件保存失败" + exception.toString());
                    return;
                }
                JOptionPane.showMessageDialog(this, "添加成功");
                dispose();
            }
        });
        ok.addActionListener(e -> dispose());
    }
}
