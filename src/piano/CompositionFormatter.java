package piano;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public abstract class CompositionFormatter implements ExportableComposition {
    protected String directory;
    protected ExportableComposition decoratedComposition;

    public CompositionFormatter(ExportableComposition decoratedComposition, String s) {
        directory = s;
        this.decoratedComposition = decoratedComposition;
    }

    public void exportComposition() throws FileNotFoundException, UnsupportedEncodingException {
        decoratedComposition.exportComposition();
    }

    public boolean canExport() {
        return decoratedComposition.canExport();
    }
}
