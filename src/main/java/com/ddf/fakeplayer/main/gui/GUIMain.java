package com.ddf.fakeplayer.main.gui;

import com.ddf.fakeplayer.client.Client;
import com.ddf.fakeplayer.VersionInfo;
import com.ddf.fakeplayer.main.I18N;
import com.ddf.fakeplayer.main.config.Config;
import com.ddf.fakeplayer.main.Main;
import com.ddf.fakeplayer.main.config.PlayerData;
import com.ddf.fakeplayer.main.gui.dialog.AboutDialog;
import com.ddf.fakeplayer.main.gui.dialog.PlayerInfoDialog;
import com.ddf.fakeplayer.main.gui.dialog.PublicKeyDialog;
import com.ddf.fakeplayer.util.Logger;
import com.ddf.fakeplayer.util.Pair;
import com.ddf.fakeplayer.main.Util;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class GUIMain extends Main {
	private PlayersTableModel playersTableModel;
	private final JFrame frame;
	private JPanel content;
	private JScrollPane playersTableScrollPane;
	private JTable playersTable;
	private JPanel configPanel;
	private JTextField serverAddress;
	private JTextField serverPort;
	private JButton publicKeyButton;
	private JCheckBox autoReconnect;
	private JPanel webSocketPanel;
	private JCheckBox webSocketEnabled;
	private JTextField webSocketPort;
	private JButton addFakePlayer;
	private JButton connectAll;
	private JButton disconnectAll;
	private JScrollPane logScrollPane;
	private JTextArea log;
	private JPopupMenu playersTableMenu1;
	private JPopupMenu playersTableMenu2;
	private JPopupMenu trayIconMenu;
	private JDialog trayIconMenuDialog;
	private TrayIcon trayIcon;

	private GUIMain(Config config) throws IOException {
		super(config);
		frame = new JFrame("FakePlayer " + VersionInfo.VERSION);
//		frame.setIconImage(Resources.ICON);
		frame.setSize(560, 480);
//		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		initMenuBar();
		initPopupMenu();
//		initTrayIcon();
		initLayout();
		initListener();

        try {
            logger.log(I18N.get("log.configFileLoaded"), " ", config.getConfigPath().toRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            config.save();
        }
		initData();
	}

	@Override
	public void initLogger() {
		Logger.init(new Logger() {
			@Override
			public synchronized void log(Object... objects) {
				SwingUtilities.invokeLater(() -> {
					StringBuilder stringBuilder = new StringBuilder();
					for (Object obj : objects) {
						if (config.isDebug() && obj instanceof Throwable) {
							stringBuilder.append(Util.getStackTrace((Throwable) obj));
							continue;
						}
						stringBuilder.append(obj);
					}
					String text = log.getText();
					text += LocalDateTime.now().format(Logger.FORMATTER) + stringBuilder.toString() + "\n";
					log.setText(text);
					log.setCaretPosition(log.getText().length());
					logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum());
				});
			}
		});
	}

	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu(I18N.get("guiMain.menu.file"));
		menuBar.add(fileMenu);

		JMenuItem exportConfig = new JMenuItem(I18N.get("guiMain.menu.file.exportConfig"));
		exportConfig.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(I18N.get("guiMain.fileFilter.description.configFile"), "yaml"));
			int result = fileChooser.showSaveDialog(frame);
			if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
				if (!path.endsWith(".yaml")) {
				    path += ".yaml";
				}
                try {
                    config.save(Paths.get(path));
                } catch (IOException ioException) {
                    logger.log(I18N.get("log.exportConfigFail") + " ", ioException);
                    return;
                }
                logger.log(I18N.get("log.exportConfigSucceed"));
            }
		});
		fileMenu.add(exportConfig);

		JMenuItem importConfig = new JMenuItem(I18N.get("guiMain.menu.file.importConfig"));
		importConfig.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(I18N.get("guiMain.fileFilter.description.configFile"), "yaml"));
			int result = fileChooser.showOpenDialog(frame);
			if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    Config conf = Config.load(fileChooser.getSelectedFile().toPath());
                    conf.setConfigPath(config.getConfigPath());
                    setConfig(conf);
                    conf.save();
                    clients.forEach(Client::close);
                    initData();
                } catch (IOException ioException) {
                    logger.log(I18N.get("log.importConfigFail"), " ", ioException);
                    return;
                }
                logger.log(I18N.get("log.importConfigSucceed"));
            }
		});
		fileMenu.add(importConfig);
		fileMenu.addSeparator();

		JMenuItem saveConfig = new JMenuItem(I18N.get("guiMain.menu.file.saveConfig"));
		saveConfig.addActionListener(e -> saveConfig());
		fileMenu.add(saveConfig);
		fileMenu.addSeparator();

//		JMenuItem settings = new JMenuItem("设置");
//		settings.addActionListener(e -> {
//
//		});
//		fileMenu.add(settings);
//		fileMenu.addSeparator();

		JMenuItem exit = new JMenuItem(I18N.get("guiMain.menu.file.exit"));
		exit.addActionListener(e -> exit());
		fileMenu.add(exit);

		JMenu helpMenu = new JMenu(I18N.get("guiMain.menu.help"));
		menuBar.add(helpMenu);

		JMenuItem showHelp = new JMenuItem(I18N.get("guiMain.menu.help.showHelp"));
		showHelp.addActionListener(e -> Util.tryOpenBrowser("https://github.com/ddf8196/FakePlayer/wiki"));
		helpMenu.add(showHelp);
		helpMenu.addSeparator();

		JMenuItem about = new JMenuItem(I18N.get("guiMain.menu.help.about"));
		about.addActionListener(e -> {
			AboutDialog dialog = new AboutDialog(this);
			dialog.setVisible(true);
		});
		helpMenu.add(about);
	}

	private void initPopupMenu() {
		playersTableMenu1 = new JPopupMenu();
		JMenuItem connect = new JMenuItem(I18N.get("guiMain.popupMenu.playersTable.connect"));
		connect.addActionListener(e -> getSelectedClient().connect(config.getServerAddress(), config.getServerPort()));
		playersTableMenu1.add(connect);

		JMenuItem disconnect = new JMenuItem(I18N.get("guiMain.popupMenu.playersTable.disconnect"));
		disconnect.addActionListener(e -> getSelectedClient().stop());
		playersTableMenu1.add(disconnect);

		JMenuItem edit = new JMenuItem(I18N.get("guiMain.popupMenu.playersTable.edit"));
		edit.addActionListener(e -> showEditPlayerDialog());
		playersTableMenu1.add(edit);

		JMenuItem remove = new JMenuItem(I18N.get("guiMain.popupMenu.playersTable.remove"));
		remove.addActionListener(e -> removePlayer(getSelectedClient().getPlayerName()));
		playersTableMenu1.add(remove);

		playersTableMenu2 = new JPopupMenu();
		JMenuItem connectSelected = new JMenuItem(I18N.get("guiMain.popupMenu.playersTable.connectSelected"));
		connectSelected.addActionListener(e -> {
			for (int i : playersTable.getSelectedRows()) {
				Client client = getClient(playersTableModel.getClientName(playersTable.convertRowIndexToModel(i)));
				if (client != null) {
					client.connect(config.getServerAddress(), config.getServerPort());
				}
			}
		});
		playersTableMenu2.add(connectSelected);

		JMenuItem disconnectSelected = new JMenuItem(I18N.get("guiMain.popupMenu.playersTable.disconnectSelected"));
		disconnectSelected.addActionListener(e -> {
			for (int i : playersTable.getSelectedRows()) {
				Client client = getClient(playersTableModel.getClientName(playersTable.convertRowIndexToModel(i)));
				if (client != null) {
					client.stop();
				}
			}
		});
		playersTableMenu2.add(disconnectSelected);

		JMenuItem removeSelected = new JMenuItem(I18N.get("guiMain.popupMenu.playersTable.removeSelected"));
		removeSelected.addActionListener(e -> {
			List<String> list = new ArrayList<>();
			for (int i : playersTable.getSelectedRows()) {
				list.add(playersTableModel.getClientName(playersTable.convertRowIndexToModel(i)));
			}
			list.forEach(this::removePlayer);
		});
		playersTableMenu2.add(removeSelected);
	}

	private void initLayout() {
		content = new JPanel();
		playersTableScrollPane = new JScrollPane();
		playersTable = new JTable();
		JPanel right = new JPanel();
		configPanel = new JPanel();
		JLabel serverAddressLabel = new JLabel(I18N.get("guiMain.label.serverAddress"));
		serverAddress = new JTextField();
		JLabel serverPortLabel = new JLabel(I18N.get("guiMain.label.serverPort"));
		serverPort = new JTextField();
		JLabel publicKeyLabel = new JLabel(I18N.get("guiMain.label.publicKey"));
		publicKeyButton = new JButton(I18N.get("guiMain.button.publicKey"));
		autoReconnect = new JCheckBox(I18N.get("guiMain.checkBox.autoReconnect"));
		JSeparator separator1 = new JSeparator();
		webSocketPanel = new JPanel();
		webSocketEnabled = new JCheckBox(I18N.get("guiMain.checkBox.webSocketEnabled"));
		JLabel webSocketPortLabel = new JLabel(I18N.get("guiMain.label.webSocketPort"));
		webSocketPort = new JTextField();
		JSeparator separator2 = new JSeparator();
		addFakePlayer = new JButton(I18N.get("guiMain.button.addFakePlayer"));
		connectAll = new JButton(I18N.get("guiMain.button.connectAll"));
		disconnectAll = new JButton(I18N.get("guiMain.button.disconnectAll"));
		logScrollPane = new JScrollPane();
		log = new JTextArea();

		content.setLayout(new MigLayout("hidemode 3", "[275,grow,fill][275,grow,fill]rel", "[275,grow,fill][125,grow,fill]"));
		frame.setContentPane(content);
		playersTable.setAutoCreateRowSorter(true);
		playersTable.setShowHorizontalLines(false);
		playersTable.setBorder(BorderFactory.createEmptyBorder());
		playersTableScrollPane.setViewportView(playersTable);
		content.add(playersTableScrollPane, "cell 0 0");
		right.setLayout(new MigLayout("insets 0,hidemode 3", "[100,grow,fill][100,grow,fill]", "[][grow][][grow][][]"));
		configPanel.setLayout(new MigLayout("insets 0,hidemode 3", "[fill][169,grow,fill]", "[][][][]"));
		configPanel.add(serverAddressLabel, "cell 0 0");
		configPanel.add(serverAddress, "cell 1 0");
		configPanel.add(serverPortLabel, "cell 0 1");
		configPanel.add(serverPort, "cell 1 1");
		configPanel.add(publicKeyLabel, "cell 0 2");
		configPanel.add(publicKeyButton, "cell 1 2");
		configPanel.add(autoReconnect, "cell 0 3 2 1");
		right.add(configPanel, "cell 0 0 2 1");
		right.add(separator1, "cell 0 1 2 1");
		webSocketPanel.setLayout(new MigLayout("insets 0,hidemode 3", "[fill][grow,fill]", "[][]"));
		webSocketPanel.add(webSocketEnabled, "cell 0 0 2 1");
		webSocketPanel.add(webSocketPortLabel, "cell 0 1");
		webSocketPanel.add(webSocketPort, "cell 1 1");
		right.add(webSocketPanel, "cell 0 2 2 1");
		right.add(separator2, "cell 0 3 2 1");
		right.add(addFakePlayer, "cell 0 4 2 1");
		right.add(connectAll, "cell 0 5");
		right.add(disconnectAll, "cell 1 5");
		content.add(right, "cell 1 0");
		log.setEditable(false);
		logScrollPane.setViewportView(log);
		content.add(logScrollPane, "cell 0 1 2 1,growy");
	}

//	private void initTrayIcon() {
//		if (!Util.isSystemTraySupported()) {
//			return;
//		}
//		try {
//			trayIconMenuDialog = new JDialog();
//			trayIconMenuDialog.setSize(0, 0);
//			trayIconMenuDialog.setUndecorated(true);
//
//			trayIconMenu = new JPopupMenu() {
//				@Override
//				protected void firePopupMenuWillBecomeInvisible() {
//					super.firePopupMenuWillBecomeInvisible();
//					trayIconMenuDialog.setVisible(false);
//				}
//			};
//
//			JMenuItem showMainFrame = new JMenuItem("显示主界面");
//			showMainFrame.addActionListener(e -> showMainFrame());
//			trayIconMenu.add(showMainFrame);
//
//			JMenuItem exit = new JMenuItem("退出");
//			exit.addActionListener(e -> exit());
//			trayIconMenu.add(exit);
//
//			trayIcon = new TrayIcon(Resources.ICON, "FakePlayer " + VersionInfo.VERSION);
//			trayIcon.setImageAutoSize(true);
//			trayIcon.addMouseListener(new MouseAdapter() {
//				@Override
//				public void mouseClicked(MouseEvent e) {
//					if (SwingUtilities.isLeftMouseButton(e)) {
//						showMainFrame();
//					} else if (SwingUtilities.isRightMouseButton(e)) {
//						showTrayIconMenu(e.getXOnScreen(), e.getYOnScreen());
//					}
//				}
//			});
//
//			SystemTray systemTray = SystemTray.getSystemTray();
//			systemTray.add(trayIcon);
//		} catch (Throwable throwable) {
//			throwable.printStackTrace();
//			if (Util.isSystemTraySupported()) {
//				SystemTray.getSystemTray().remove(trayIcon);
//			}
//			trayIcon = null;
//		}
//	}

	private void initListener() {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				content.requestFocus();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				if (trayIcon != null) {
					frame.setVisible(false);
				} else {
					exit();
				}
			}
		});
		playersTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				playersTable.requestFocus();
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					showEditPlayerDialog();
				} else if (SwingUtilities.isRightMouseButton(e)) {
					int row = playersTable.rowAtPoint(e.getPoint());
					if (row == -1) {
						return;
					}
					boolean inSelected = false;
					for (int r : playersTable.getSelectedRows()) {
						if (row == r) {
							inSelected = true;
							break;
						}
					}
					if (!inSelected) {
						playersTable.setRowSelectionInterval(row, row);
					}
					if (playersTable.getSelectedRowCount() > 1) {
						playersTableMenu2.show(playersTable, e.getX(), e.getY());
					} else {
						playersTableMenu1.show(playersTable, e.getX(), e.getY());
					}
				}
			}
		});
		serverAddress.addActionListener(e -> content.requestFocus());
		serverAddress.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String address = config.getServerAddress();
				saveConfig();
				if (!config.getServerAddress().equals(address)) {
					new ReconnectAllWorker().execute();
				}
			}
		});
		serverPort.addActionListener(e -> content.requestFocus());
		serverPort.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int port = config.getServerPort();
				saveConfig();
				if (port != config.getServerPort()) {
					new ReconnectAllWorker().execute();
				}
			}
		});
		publicKeyButton.addActionListener(e -> {
			PublicKeyDialog dialog = new PublicKeyDialog(this);
			dialog.setVisible(true);
		});
		autoReconnect.addChangeListener(e -> clients.forEach(client -> client.setAutoReconnect(autoReconnect.isSelected())));
		webSocketEnabled.addItemListener(e -> setWebSocketEnabled(webSocketEnabled.isSelected()));
		webSocketPort.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					int port = Integer.parseInt(webSocketPort.getText());
					if (!Util.isValidPort(port)) {
						JOptionPane.showMessageDialog(frame, I18N.get("guiMain.message.invalidPort"));
						webSocketPort.setText(Integer.toString(config.getWebSocketPort()));
						webSocketPort.requestFocus();
					}
					updateWebSocketPort(port);
				} catch (NumberFormatException exception) {
					webSocketPort.setText(Integer.toString(config.getWebSocketPort()));
					webSocketPort.requestFocus();
				}
			}
		});
		webSocketPort.addActionListener(e -> content.requestFocus());
		addFakePlayer.addActionListener(e -> {
			PlayerInfoDialog dialog = new PlayerInfoDialog(this);
			dialog.setVisible(true);
		});
		connectAll.addActionListener(e -> new ConnectAllWorker().execute());
		disconnectAll.addActionListener(e -> clients.forEach(Client::stop));
	}

	private void initData() {
		clients.clear();
		playersTableModel = new PlayersTableModel();

		webSocketEnabled.setSelected(config.isWebSocketEnabled());
		webSocketPort.setDocument(new NumberDocument());
		webSocketPort.setText(Integer.toString(config.getWebSocketPort()));

		config.getPlayerDataList().forEach(this::addClient);
		playersTable.setModel(playersTableModel);
		playersTable.getRowSorter().setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));

		serverAddress.setText(config.getServerAddress());
		serverPort.setDocument(new NumberDocument());
		serverPort.setText(Integer.toString(config.getServerPort()));
		autoReconnect.setSelected(config.isAutoReconnect());
	}

	private void showEditPlayerDialog() {
		Client client = getSelectedClient();
		PlayerInfoDialog dialog = new PlayerInfoDialog(GUIMain.this, client.getPlayerName());
		dialog.setVisible(true);
	}

	private Client getSelectedClient() {
		int selectedRow = playersTable.getSelectedRow();
		if (selectedRow < 0) {
			selectedRow = 0;
		} else {
			selectedRow = playersTable.convertRowIndexToModel(selectedRow);
		}
		return getClient(playersTableModel.getClientName(selectedRow));
	}

	@Override
	public Client addClient(PlayerData playerData) {
		Client client = super.addClient(playerData);
		SwingUtilities.invokeLater(() -> playersTableModel.addClientInfo(playerData.getName()));
		client.addStateChangedListener((client1, oldState, currentState) ->
			SwingUtilities.invokeLater(() ->
				playersTableModel.setClientState(client1.getPlayerName(), currentState)
			)
		);
		return client;
	}

	@Override
	public void removeClient(String name) {
		super.removeClient(name);
		SwingUtilities.invokeLater(() -> playersTableModel.removeClientInfo(name));
	}

	@Override
	public void setWebSocketEnabled(boolean enabled) {
		super.setWebSocketEnabled(enabled);
		webSocketPort.setEnabled(!enabled);
	}

	public void saveConfig() {
		if (!config.isConfigured()) {
			config.setConfigured(true);
		}
		config.setServerAddress(serverAddress.getText());
		try {
			int serverPort1 = Integer.parseInt(serverPort.getText());
			if (Util.isValidPort(serverPort1)) {
				config.setServerPort(serverPort1);
			}
		} catch (NumberFormatException ignored) {}
		config.setAutoReconnect(autoReconnect.isSelected());
		config.setWebSocketEnabled(webSocketEnabled.isSelected());
		try {
			int webSocketPort1 = Integer.parseInt(webSocketPort.getText());
			if (Util.isValidPort(webSocketPort1)) {
				config.setWebSocketPort(webSocketPort1);
			}
		} catch (NumberFormatException ignored) {}
		try {
			config.save();
		} catch (IOException e) {
			logger.log(I18N.get("log.saveConfigFail"), " ", e);
			e.printStackTrace();
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	public void showMainFrame() {
		frame.setVisible(true);
		frame.setExtendedState(Frame.NORMAL);
		frame.requestFocus();
	}

//	public void showTrayIconMenu(int x, int y) {
//		trayIconMenuDialog.setVisible(true);
//		trayIconMenu.setInvoker(trayIconMenuDialog);
//		trayIconMenu.setVisible(true);
//		Point point = new Point(x, y);
//		int menuHeight = trayIconMenu.getHeight();
//		if (y - menuHeight >= 0 || y + menuHeight > Toolkit.getDefaultToolkit().getScreenSize().height) {
//			point.y = y - menuHeight;
//		}
//		trayIconMenu.setLocation(point);
//		trayIconMenuDialog.setLocation(point);
//	}

	public void exit() {
		clients.forEach(Client::close);
		saveConfig();
		stopWebSocket();
		System.exit(0);
	}

	public static class NumberDocument extends PlainDocument {
		private final Pattern pattern = Pattern.compile("^[0-9]*$");
		@Override
		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str == null){
				return;
			}
			String tmp = getText(0, offset).concat(str);
			Matcher matcher = pattern.matcher(tmp);
			if(matcher.matches()) {
				super.insertString(offset, str, attr);
			}
		}
	}

	public static class PlayersTableModel extends AbstractTableModel {
		private final List<Pair<String, Client.State>> clientInfoList = new ArrayList<>();

		@Override
		public int getRowCount() {
			return clientInfoList.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
				case 0:
					return I18N.get("guiMain.playersTable.name");
				case 1:
					return I18N.get("guiMain.playersTable.status");
				default:
					return "";
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0:
					return clientInfoList.get(rowIndex).getKey();
				case 1:
					switch (clientInfoList.get(rowIndex).getValue()) {
						case CONNECTING:
							return I18N.get("state.connecting");
						case CONNECTED:
							return I18N.get("state.connected");
						case DISCONNECTING:
							return I18N.get("state.disconnecting");
						case DISCONNECTED:
							return I18N.get("state.disconnected");
						case RECONNECTING:
							return I18N.get("state.reconnecting");
						case STOPPING:
							return I18N.get("state.stopping");
						case STOPPED:
							return I18N.get("state.stopped");
					}
			}
			return null;
		}

		public void addClientInfo(String name) {
			if (containsClientInfo(name)) {
				return;
			}
			Pair<String, Client.State> pair = new Pair<>(name, Client.State.STOPPED);
			clientInfoList.add(pair);
			fireTableDataChanged();
		}

		public void removeClientInfo(String name) {
			if (!containsClientInfo(name)) {
				return;
			}
			clientInfoList.removeIf(pair -> pair.getKey().equals(name));
			fireTableDataChanged();
		}

		public void setClientState(String name, Client.State state) {
			Pair<String, Client.State> pair = getClientInfo(name);
			if (pair != null) {
				pair.setValue(state);
				fireTableCellUpdated(clientInfoList.indexOf(pair), 1);
			}
		}

		public Pair<String, Client.State> getClientInfo(String name) {
			for (Pair<String, Client.State> pair : clientInfoList) {
				if (pair.getKey().equals(name)) {
					return pair;
				}
			}
			return null;
		}

		public String getClientName(int index) {
			return clientInfoList.get(index).getKey();
		}

		public Client.State getClientState(int index) {
			return clientInfoList.get(index).getValue();
		}

		public boolean containsClientInfo(String name) {
			return getClientInfo(name) != null;
		}

		public void clear() {
			clientInfoList.clear();
			fireTableDataChanged();
		}
	}

	public class ConnectAllWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() {
			new ArrayList<>(clients).forEach(client -> {
				client.connect(config.getServerAddress(), config.getServerPort());
			});
			return null;
		}
	}

	public class ReconnectAllWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() {
			List<Client> clientsCopy = new ArrayList<>(clients);
			clientsCopy.forEach(client -> client.setStop(true));
			clientsCopy.forEach(client -> {
				client.stop(true);
				client.connect(config.getServerAddress(), config.getServerPort());
			});
			return null;
		}
	}

    public static void main(Config config) {
	    SwingUtilities.invokeLater(() -> {
	        try {
	        	switch (config.getTheme()) {
					case "light":
						FlatLightLaf.setup();
						break;
					case "dark":
						FlatDarkLaf.setup();
						break;
					case "darcula":
						FlatDarculaLaf.setup();
						break;
					default:
						break;
				}
                GUIMain guiMain = new GUIMain(config);
                guiMain.getFrame().setVisible(true);
            } catch (Exception e) {
	            e.printStackTrace();
            }
        });
    }
}
