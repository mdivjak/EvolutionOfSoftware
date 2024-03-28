package piano;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public abstract class Formatter implements ExportableComposition {
	
	protected String directory;

	protected ExportableComposition decoratedComposition;

	public Formatter(ExportableComposition composition, String s) {
		directory = s;
		decoratedComposition = composition;
	}

	@Override
	public boolean canExport() {
		return decoratedComposition.canExport();
	}

	@Override
	public void exportComposition() throws FileNotFoundException, UnsupportedEncodingException {
		decoratedComposition.exportComposition();
	}
}
