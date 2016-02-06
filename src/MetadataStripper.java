import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MetadataStripper extends JFrame {
	private File file;
	
	public MetadataStripper() {
		super("Metadata Stripper");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(getParent());
		setFocusable(true);
		setLookAndFeel();
		
		init();
		pack();
		setSize(700, 200);
		setResizable(false);
		
		file = null;
	}
	
	private void init() {
		final Container content = getContentPane();
		content.setLayout(new BorderLayout());
		
		//top
		final JPanel top = new JPanel(new FlowLayout());
		JLabel text = new JLabel("Select a picture or a folder of pictures.");
		
		top.add(text);
		content.add(top, BorderLayout.NORTH);
		
		//middle
		final JPanel middle = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JTextField dir = new JTextField();
		dir.setEditable(true);
		//dir.setBounds(dir.getX(), dir.getY(), 50, dir.getHeight());
		dir.setColumns(30);
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fc.showOpenDialog(MetadataStripper.this);
				file = fc.getSelectedFile();
				dir.setText(file.getAbsolutePath());
			}
		});
		middle.add(dir);
		middle.add(browse);
		
		content.add(middle, BorderLayout.CENTER);
		
		
		//bottom
		final JPanel bottom = new JPanel(new FlowLayout());
		JButton run = new JButton("Run");
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Execute(file);
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		bottom.add(run);
		bottom.add(cancel);
		
		content.add(bottom, BorderLayout.SOUTH);
	}
	
	private void Execute(File file) {		
		if(file.isFile() && file.getName().endsWith(".JPG")) {
			File newDir = new File(file.getParent() + "\\Stripped_Pics");
			newDir.mkdir();
			File newFile = new File(newDir + "\\" + file.getName());
			try {
				removeExifMetadata(file, newFile);
			} catch (ImageReadException | ImageWriteException | IOException e) {
				e.printStackTrace();
			}
		}
		else if(file.isDirectory()) {
			File newDir = new File(file.getParentFile().getAbsolutePath() + "\\Stripped_Pics");
			newDir.mkdir();
			File[] files = file.listFiles();
			for(File origin : files) {
				if(origin.isFile() && origin.getName().endsWith(".JPG"))
					try {
						File temp = new File(newDir + "\\" + origin.getName());
						removeExifMetadata(origin, temp);
					} catch (ImageReadException | ImageWriteException | IOException e) {
						e.printStackTrace();
					}
			}
		}
		else if(true) {
			System.err.println("The .jpg is incorrect");
			System.exit(0);
		}
	}
	
	private void removeExifMetadata(final File jpegImageFile, final File dst)
	        throws IOException, ImageReadException, ImageWriteException {
	    OutputStream os = null;
	    try {
	        os = new FileOutputStream(dst);
	        os = new BufferedOutputStream(os);

	        new ExifRewriter().removeExifMetadata(jpegImageFile, os);
	    } finally {
	        if (os != null) {
	            try {
	                os.close();
	            } catch (final IOException e) {
	            	e.printStackTrace();
	            }
	        }
	    }
	}
	
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(args.length > 0) {
			System.err.println("You cannot pass arguments into this program!");
			System.exit(0);
		}
		
		MetadataStripper run = new MetadataStripper();
	}
}