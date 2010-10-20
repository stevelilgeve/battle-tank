
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

public class Options extends Form implements CommandListener {
	
	private int difficulty;
	
	public static final int EASY = 0;
	public static final int MEDIUM = 1;
	public static final int HARD = 2;

	private Display display;
	private Displayable prev;

	private ChoiceGroup difficultyGroup;

	private Command ok;
	private Command cancel;

	public Options(Display display, Displayable prev) {
		super("Options");

		this.display = display;
		this.prev = prev;

		append("Difficulty:");
		
		setDifficulty(MEDIUM);

		difficultyGroup = new ChoiceGroup(null, Choice.EXCLUSIVE);
		difficultyGroup.append("easy", null);
		difficultyGroup.append("medium", null);
		difficultyGroup.append("hard", null);
		append(difficultyGroup);

		ok = new Command("OK", Command.OK, 0);
		cancel = new Command("Cancel", Command.CANCEL, 1);
		
		loadUI();

		addCommand(ok);
		addCommand(cancel);
		setCommandListener(this);
	}

	/**
	 * Initialize UI from current settings.
	 */
	private void loadUI() {
		difficultyGroup.setSelectedIndex(getDifficulty(), true);		
	}

	/**
	 * Apply UI settings.
	 */
	private void readUI() {
		setDifficulty(difficultyGroup.getSelectedIndex());
		
	}

	public int getDifficulty() {
		return difficulty;
	}

	protected void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	/*
	 *  (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command c, Displayable d) {
		if (c == ok) {
			readUI();
		} else if (c == cancel) {
			loadUI();
		}
		display.setCurrent(prev);
	}
	
}
