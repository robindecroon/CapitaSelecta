/**
 * 
 */
package core;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import processing.core.PApplet;

/**
 * @author Robin
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final PApplet mainApplet = new MainApplet();
		Frame frame = new Frame(Constants.APP_NAME);
		frame.setLayout(new BorderLayout());

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		Panel toolbar = new Panel();
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

		final TextField tf1 = new TextField(Constants.TEXTBOX1, 30);
		tf1.setBackground(Color.RED);
		tf1.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// if(tf1.getText() != null)
				// tf1.setText(text1);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				tf1.setText("");

			}
		});
		toolbar.add(tf1);

		final TextField tf2 = new TextField(Constants.TEXTBOX2, 30);
		tf2.setBackground(Color.GREEN);
		tf2.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// if(tf2.getText() != null)
				// tf2.setText(text2);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				tf2.setText("");

			}
		});
		toolbar.add(tf2);
		toolbar.setBackground(new Color(240, 240, 240));

		Button searchButton = new Button("Draw");
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				// List<Location> searchResults = searchKeyword(model,
				// tf1.getText());
				// if(showCountries) {
				// ((MainApplet)
				// unfoldingApplet).initCountries(searchCountry(model,
				// tf1.getText()));
				// } else {
				// ((MainApplet) unfoldingApplet).clearCountries();
				// }
				// ((MainApplet) unfoldingApplet).initLocations(searchResults);
				// List<Location> searchResults2 = searchKeyword(model,
				// tf2.getText());
				// ((MainApplet)
				// unfoldingApplet).initLocations2(searchResults2);
			}
		});
		toolbar.add(searchButton);

		mainApplet.init();
		frame.add(toolbar, BorderLayout.NORTH);
		frame.add(mainApplet, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}

}
