package piano;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public interface ExportableComposition {

    public void exportComposition() throws FileNotFoundException, UnsupportedEncodingException;
    public boolean canExport();
}