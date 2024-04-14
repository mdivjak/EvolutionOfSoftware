package piano;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import piano.gui.Piano;
import piano.gui.VisualComposition;
import piano.music.Composition;

public class Main extends Frame {
	private VisualComposition visualComposition;
	private Piano piano;
	
	private TextField loadFileName;
	private Button loadFileButton;
	private Button play, pause, stop;
	private Checkbox notes, letters;
	private Checkbox keyHelp;
	
	private TextField exportFileName;
	private Button exportFileButton;
	private Button startRecording, stopRecording;
	private Checkbox midi, text;

	// Add user
	private Button addUserButton, removeUserButton;
	private UserDialog userDialog = new UserDialog(this, "User", true);
	
	private static final Color PRIMARY_COLOR = new Color(255, 193, 7);
	private static final Color SECONDARY_COLOR = new Color(86, 86, 86);
	
	public Main() {
		super("Piano Player");
		setSize(1420, 850);
		
		addComponents();
		addListeners();
		
		setForeground(PRIMARY_COLOR);
		setBackground(SECONDARY_COLOR);
		paintButtons();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		setVisible(true);
	}

	private class UserDialog extends Dialog implements ActionListener
	{
		private Button submit;
		private TextField firstName, lastName, username;

		public UserDialog(Frame f, String s, boolean b) {
			super(f, s, b);
			setSize(500, 300);
			setLocationRelativeTo(null);

			Label l = new Label("Unesite podatke o korisniku", Label.CENTER);

			this.add(l, "North");
			Panel panel = new Panel(new GridLayout(5, 1));
			this.add(panel, "South");

			Panel pan1 = new Panel();
			pan1.add(new Label("First name:", Label.RIGHT));
			firstName = new TextField("", 50);
			pan1.add(firstName);

			Panel pan2 = new Panel();
			pan2.add(new Label("Last name:", Label.RIGHT));
			lastName = new TextField("", 50);
			pan2.add(lastName);

			Panel pan3 = new Panel();
			pan3.add(new Label("Username:", Label.RIGHT));
			username = new TextField("", 50);
			pan3.add(username);

			Label error = new Label("Podaci nisu ispravno uneti", Label.CENTER);

			error.setForeground(Color.red);
			error.setVisible(false);

			submit = new Button("Submit");
			submit.setForeground(SECONDARY_COLOR);
			submit.setBackground(PRIMARY_COLOR);
			submit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					User user = User.getInstance();
					if (!user.setData(firstName.getText(), lastName.getText(), username.getText())
							|| !user.hasData()) {
						error.setVisible(true);
					}
					else {
						firstName.setText("");
						lastName.setText("");
						username.setText("");
						error.setVisible(false);

						setVisible(false);
					}
				}
			});

			panel.add(pan1); 
			panel.add(pan2);
			panel.add(pan3);
			panel.add(submit);
			panel.add(error);

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});
		}

		public void actionPerformed(ActionEvent e) {}
	}
	
	private void addListeners() {
		loadFileButton.addActionListener(e -> {
			try {
				visualComposition.loadFromFile(loadFileName.getText());
			} catch (FileNotFoundException e1) {
				loadFileName.setText("ERROR: Failed loading file");
			}
		});
		
		play.addActionListener(e -> { visualComposition.play(); });
		pause.addActionListener(e -> { visualComposition.pause(); });
		stop.addActionListener(e -> { visualComposition.stop(); });

		addUserButton.addActionListener(e -> {
			userDialog.setVisible(true);
			addUserButton.setEnabled(false);
			removeUserButton.setEnabled(true);
		});
		removeUserButton.addActionListener(e -> {
			User.getInstance().clearData();

			addUserButton.setEnabled(true);
			removeUserButton.setEnabled(false);
		});
		removeUserButton.setEnabled(false);
		
		notes.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED)
				visualComposition.showNotes();
		});
		
		letters.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED)
				visualComposition.showLetters();
		});
		
		keyHelp.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED)
				piano.setShowHelp(true);
			else piano.setShowHelp(false);
		});
		
		startRecording.addActionListener(e -> {
			piano.startRecording();
			startRecording.setEnabled(false);
			stopRecording.setEnabled(true);
		});
		stopRecording.addActionListener(e -> {
			piano.stopRecording();
			stopRecording.setEnabled(false);
			startRecording.setEnabled(true);
		});
		stopRecording.setEnabled(false);
		
		exportFileButton.addActionListener(e -> {
			String fileName = exportFileName.getText();
			Composition composition = piano.getRecordedComposition();
			
			if(composition == null) return;

			String directory = "";
			try {
				directory = User.getExportPath(fileName);
			} catch (ExportForbiddenException e1) {
				System.err.println("Exporting is forbidden");
				return;
			}

			boolean textFormat = text.getState();
			ExportableComposition exportableComposition = null;
			exportableComposition = textFormat ? new TextFormatter(composition, directory) : new MidiFormatter(composition, directory);
			try {
				exportableComposition.exportComposition();
				startRecording.setEnabled(true);
				stopRecording.setEnabled(false);
			} catch (FileNotFoundException e1) {
				exportFileName.setText("ERROR: Failed creating file");
			} catch (UnsupportedEncodingException e1) {
				exportFileName.setText("ERROR: Failed exporting file");
			}
		});
	}
	
	private void paintButtons() {
		loadFileName.setForeground(SECONDARY_COLOR);
		exportFileName.setForeground(SECONDARY_COLOR);
		
		loadFileButton.setBackground(PRIMARY_COLOR);
		exportFileButton.setBackground(PRIMARY_COLOR);
		play.setBackground(PRIMARY_COLOR);
		pause.setBackground(PRIMARY_COLOR);
		stop.setBackground(PRIMARY_COLOR);
		addUserButton.setBackground(PRIMARY_COLOR);
		removeUserButton.setBackground(PRIMARY_COLOR);
		startRecording.setBackground(PRIMARY_COLOR);
		stopRecording.setBackground(PRIMARY_COLOR);
		
		loadFileButton.setForeground(SECONDARY_COLOR);
		exportFileButton.setForeground(SECONDARY_COLOR);
		play.setForeground(SECONDARY_COLOR);
		pause.setForeground(SECONDARY_COLOR);
		stop.setForeground(SECONDARY_COLOR);
		addUserButton.setForeground(SECONDARY_COLOR);
		removeUserButton.setForeground(SECONDARY_COLOR);
		startRecording.setForeground(SECONDARY_COLOR);
		stopRecording.setForeground(SECONDARY_COLOR);
		
	}
	
	private void addComponents() {
		Panel fullWindow = new Panel(new GridLayout(3, 1));
		
		Panel controlPanel = new Panel(new GridLayout(1, 2));
		
		Panel leftControlPanel = new Panel(new GridLayout(4, 1));
		
		Panel loadFilePanel = new Panel();
		loadFilePanel.add(new Label("Composition filepath:"));
		loadFilePanel.add(loadFileName = new TextField("jingle_bells.txt", 50));
		loadFilePanel.add(loadFileButton = new Button("Load"));
		
		leftControlPanel.add(loadFilePanel);
		
		Panel checkboxes = new Panel();
		CheckboxGroup cb = new CheckboxGroup();
		checkboxes.add(notes = new Checkbox("Notes", true, cb));
		checkboxes.add(letters = new Checkbox("Letters", false, cb));
		checkboxes.add(keyHelp = new Checkbox("Print help on piano keys"));
		
		leftControlPanel.add(checkboxes);
		
		Panel playerButtons = new Panel();
		
		playerButtons.add(play = new Button("Play"));
		playerButtons.add(pause = new Button("Pause"));
		playerButtons.add(stop = new Button("Stop"));
		
		leftControlPanel.add(playerButtons);

		Panel userButtonPanel = new Panel();
		userButtonPanel.add(addUserButton = new Button("Add user"));
		userButtonPanel.add(removeUserButton = new Button("Remove user"));
		
		leftControlPanel.add(userButtonPanel);
		
		controlPanel.add(leftControlPanel);
		
		Panel rightControlPanel = new Panel(new GridLayout(3, 1));
		
		Panel exportFilePanel = new Panel();
		exportFilePanel.add(new Label("Export file filepath:"));
		exportFilePanel.add(exportFileName = new TextField("export.txt", 50));
		exportFilePanel.add(exportFileButton = new Button("Export"));
		
		rightControlPanel.add(exportFilePanel);
		
		checkboxes = new Panel();
		cb = new CheckboxGroup();
		checkboxes.add(text = new Checkbox("Text", true, cb));
		checkboxes.add(midi = new Checkbox("Midi", false, cb));
		
		rightControlPanel.add(checkboxes);
		
		Panel recordingPanel = new Panel();
		recordingPanel.add(startRecording = new Button("Start recording"));
		recordingPanel.add(stopRecording = new Button("Stop recording"));

		rightControlPanel.add(recordingPanel);
		
		controlPanel.add(rightControlPanel);
		
		fullWindow.add(controlPanel);
		
		piano = Piano.getInstance();
		fullWindow.add(visualComposition = new VisualComposition(piano));
		fullWindow.add(piano);

		add(fullWindow);
	}
	
	public static void main(String[] args) {
		new Main();
	}
}
