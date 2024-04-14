package piano;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

import piano.music.Chord;
import piano.music.Composition;
import piano.music.MusicSymbol;
import piano.music.Note;
import piano.music.Pause;
import piano.music.Composition.NoSymbolFound;

public class TextFormatter extends Formatter {
	
	private Composition composition;

	public TextFormatter(ExportableComposition composition, String s) {
		super(composition, s);
		this.composition = (Composition) composition;
	}

	public void export(String fileName, Composition composition) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		MusicSymbol previous = null, current = null;
		for(int i = 0; i < composition.size(); i++) {
			try { current = composition.get(i); } catch (NoSymbolFound e) {}
			if(current instanceof Pause) {
				if(previous != null && previous instanceof Note && previous.getDuration() == MusicSymbol.EIGHT)
					writer.write("]");
				writer.write(current.toString());
			} else if(current instanceof Note) {
				if(current.getDuration() == MusicSymbol.FOURTH) {
					if(previous != null && previous instanceof Note && previous.getDuration() == MusicSymbol.EIGHT)
						writer.write("]");
					writer.write(NoteMaps.StringToCharacter.get(current.toString()));
				} else {
					if(previous == null || (previous != null && previous.getDuration() == MusicSymbol.FOURTH)
							|| (previous != null && previous instanceof Pause && previous.getDuration() == MusicSymbol.EIGHT)) {
						writer.write("[");
						writer.write(NoteMaps.StringToCharacter.get(current.toString()) + " ");
						if(i == composition.size() - 1) writer.write("]");
					} else if(previous != null && previous instanceof Note && previous.getDuration() == MusicSymbol.EIGHT) {
						writer.write(NoteMaps.StringToCharacter.get(current.toString()) + " ");
						if(i == composition.size() - 1) writer.write("]");
					}
				}
			} else if(current instanceof Chord) {
				if(previous != null && previous instanceof Note && previous.getDuration() == MusicSymbol.EIGHT)
					writer.write("]");
				StringBuilder sb = new StringBuilder("[");
				for(int j = 0; j < ((Chord) current).size(); j++) {
					sb.append(NoteMaps.StringToCharacter.get(((Chord) current).get(j).toString()));
				}
				sb.append("]");
				writer.write(sb.toString());
			}
			previous = current;
		}
		writer.close();
	}

	@Override
	public boolean canExport() {
		return composition.canExport();
	}

	@Override
	public void exportComposition() throws FileNotFoundException, UnsupportedEncodingException {
		if(!canExport()) {
			System.err.println("Eksportovanje je zabranjeno");
			return;
		}
		
		try {
			Files.createDirectories(Paths.get(User.getInstance().getUsername()));
			export(directory, composition);
		} catch (IOException e) {
			System.err.println("Directory does not exist");
		}
	}

	public String appendSignature() {
		throw new UnsupportedOperationException();
	}
}