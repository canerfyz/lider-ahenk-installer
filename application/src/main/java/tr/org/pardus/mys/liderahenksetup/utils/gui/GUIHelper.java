package tr.org.pardus.mys.liderahenksetup.utils.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import tr.org.pardus.mys.liderahenksetup.utils.FontProvider;

public class GUIHelper {

	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static Text createText(Composite parent) {
		return createText(parent, new GridData(GridData.FILL, GridData.FILL,
				true, true));
	}

	/**
	 * 
	 * @param parent
	 * @param layoutData
	 * @return
	 */
	public static Text createText(Composite parent, Object layoutData) {
		return createText(parent, layoutData, SWT.NONE | SWT.BORDER | SWT.SINGLE);
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static Text createPasswordText(Composite parent) {
		return createPasswordText(parent, new GridData(GridData.FILL,
				GridData.FILL, true, true));
	}
	
	/**
	 * 
	 * This method creates a text with given style.
	 * (e.g. for style parameter: 
	 * SWT.NONE | SWT.SINGLE | SWT.PASSWORD)
	 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
	 * @param parent
	 * @param layoutData
	 * @param style
	 * @return 
	 */
	public static Text createText(Composite parent, Object layoutData, int style) {
		Text t = new Text(parent, style);
		t.setLayoutData(layoutData);
		t.setBackground(getApplicationBackground());
		t.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		t.setFont(FontProvider.getInstance().get(FontProvider.INPUT_FONT));
		return t;
	}

	/**
	 * 
	 * @param parent
	 * @param layoutData
	 * @return
	 */
	public static Text createPasswordText(Composite parent, Object layoutData) {
		Text t = new Text(parent, SWT.NONE | SWT.BORDER | SWT.SINGLE
				| SWT.PASSWORD);
		t.setLayoutData(layoutData);
		t.setBackground(getApplicationBackground());
		t.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		t.setFont(FontProvider.getInstance().get(FontProvider.INPUT_FONT));
		return t;
	}

	/**
	 * 
	 * @param parent
	 * @param numColumns
	 * @return
	 */
	public static Composite createComposite(Composite parent, int numColumns) {
		return createComposite(parent, new GridLayout(numColumns, false),
				new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	/**
	 * 
	 * @param parent
	 * @param layout
	 * @param layoutData
	 * @return
	 */
	public static Composite createComposite(Composite parent, Layout layout,
			Object layoutData) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(layoutData);
		c.setLayout(layout);
		c.setBackground(getApplicationBackground());
		return c;
	}

	/**
	 * 
	 * @param parent
	 * @param numColumns
	 * @return
	 */
	public static Group createGroup(Composite parent, int numColumns) {
		return createGroup(parent, new GridLayout(numColumns, false),
				new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	/**
	 * 
	 * @param parent
	 * @param layout
	 * @param layoutData
	 * @return
	 */
	public static Group createGroup(Composite parent, Layout layout,
			Object layoutData) {
		Group g = new Group(parent, SWT.NONE);
		g.setLayoutData(layoutData);
		g.setLayout(layout);
		g.setBackground(getApplicationBackground());
		return g;
	}

	/**
	 * 
	 * @param parent
	 * @param buttonType
	 * @return
	 */
	public static Button createButton(Composite parent, int buttonType) {
		return createButton(parent, buttonType, null);
	}

	/**
	 * 
	 * @param parent
	 * @param buttonType
	 * @param text
	 * @return
	 */
	public static Button createButton(Composite parent, int buttonType,
			String text) {
		Button b = new Button(parent, buttonType | SWT.BORDER);
		b.setText(text);
		b.setBackground(getApplicationBackground());
		b.setFont(FontProvider.getInstance().get(FontProvider.LABEL_FONT));
		return b;
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static Label createLabel(Composite parent) {
		return createLabel(parent, null);
	}

	/**
	 * 
	 * @param parent
	 * @param text
	 * @return
	 */
	public static Label createLabel(Composite parent, String text) {
		Label l = new Label(parent, SWT.NONE);
		l.setText(text);
		l.setBackground(getApplicationBackground());
		l.setFont(FontProvider.getInstance().get(FontProvider.LABEL_FONT));
		return l;
	}

	public static Color getApplicationBackground() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}

}
