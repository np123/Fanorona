package view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager2;

import javax.swing.JButton;

public class Layout implements LayoutManager2  {

	final static int windowWidth;
	final static int windowHeight;
	
	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		windowWidth = (int) gd.getDefaultConfiguration().getBounds().getWidth();
		windowHeight = (int) gd.getDefaultConfiguration().getBounds().getHeight();
	}
	
	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
		
	}

	@Override
	public void layoutContainer(Container arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dimension minimumLayoutSize(Container arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension preferredLayoutSize(Container arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLayoutComponent(Component arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLayoutComponent(Component arg0, Object arg1) {
		// TODO Auto-generated method stub
		JButton button = (JButton) arg0;
		if (button.getName().equals("END"))
			button.setBounds(9*windowWidth/10, 1*windowHeight/2, 100, 40);
		
	}

	@Override
	public float getLayoutAlignmentX(Container arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void invalidateLayout(Container arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dimension maximumLayoutSize(Container arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
