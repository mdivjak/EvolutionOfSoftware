package piano.music;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import piano.ExportableComposition;
import piano.Iterator;
import piano.List;
import piano.NoteMaps;
import piano.SymbolList;

public class Composition implements ExportableComposition {

	// ITERATOR
	private List symbolList = new SymbolList();
	private Iterator iterator = symbolList.createIterator();
	
	public class NoSymbolFound extends Exception {}
	
	public int size() { return symbolList.size(); }
	
	public void add(MusicSymbol symbol) { symbolList.add(symbol); }
	
	public MusicSymbol get(int i) throws NoSymbolFound {
		if(i < 0 || i >= symbolList.size()) throw new NoSymbolFound();
		return (MusicSymbol) symbolList.get(i);
	}
	
	public MusicSymbol remove(int i) throws NoSymbolFound {
		if(i < 0 || i >= symbolList.size()) throw new NoSymbolFound();
		MusicSymbol symbol = (MusicSymbol) symbolList.get(i);
		symbolList.remove(i);
		return symbol;
	}
	
	private void parseNotesAndPauses(String line) {
		for(int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if(c == '|') symbolList.add(new Pause(MusicSymbol.FOURTH));
			else if(c == ' ') symbolList.add(new Pause(MusicSymbol.EIGHT));
			else symbolList.add(new Note(NoteMaps.CharacterToString.get(c), MusicSymbol.FOURTH));
		}
	}
	
	private void parseChordsAndEights(String line) {
		if(line.length() == 1) {
			symbolList.add(new Note(NoteMaps.CharacterToString.get(line.charAt(0)), MusicSymbol.EIGHT));
			return;
		}
		if(line.charAt(1) == ' ') {
			for(int i = 0; i < line.length(); i += 2) {
				symbolList.add(new Note(NoteMaps.CharacterToString.get(line.charAt(i)), MusicSymbol.EIGHT));
			}
		} else {
			Chord chord = new Chord();
			for(int i = 0; i < line.length(); i++)
				chord.add(NoteMaps.CharacterToString.get(line.charAt(i)));
			symbolList.add(chord);
		}
	}
	
	public void loadFromFile(String fileName) throws FileNotFoundException {
		symbolList.clear();
		File file = new File(fileName);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Stream<String> stringStream = reader.lines();
		
		stringStream.forEach( line -> {
			Pattern pattern = Pattern.compile("([^\\[]*)([^\\]]*)(.*)");
			Matcher matcher = pattern.matcher(line);
			
			if (matcher.matches()) {
				String notesAndPauses = matcher.group(1);
				if (notesAndPauses.length() > 0) {
					parseNotesAndPauses(notesAndPauses);
				}
				String chordsAndEights = matcher.group(2);
				String leftover = matcher.group(3);
				if (chordsAndEights.length() > 0) {
					chordsAndEights = chordsAndEights.substring(1);
					leftover = leftover.substring(1);
				}				
				if (chordsAndEights.length() > 0) {
					parseChordsAndEights(chordsAndEights);
				}				
				while (leftover.length() > 0) {
					String newLine = leftover;
					pattern = Pattern.compile("([^\\[]*)([^\\]]*)(.*)");
					matcher = pattern.matcher(newLine);
					if (matcher.matches()) {
						notesAndPauses = matcher.group(1);
						if (notesAndPauses.length() > 0) {
							parseNotesAndPauses(notesAndPauses);
						}
						chordsAndEights = matcher.group(2);
						leftover = matcher.group(3);
						if (chordsAndEights.length() > 0) {
							chordsAndEights = chordsAndEights.substring(1);
							leftover = leftover.substring(1);
						}
						if (chordsAndEights.length() > 0) {
							parseChordsAndEights(chordsAndEights);
						}
					}
				}
			}
		});
		try {
			reader.close();
		} catch (IOException e) {
			System.err.println("Greska pri zatvaranju readera");
		}	
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for(iterator.firstElement(); iterator.hasNext(); iterator.nextElement()) {
			MusicSymbol ms = (MusicSymbol) iterator.getElement();
			sb.append(ms.toString());
		}

		return sb.toString();
	}

	@Override
	public void exportComposition() {}

	@Override
	public boolean canExport() {
		return true;
	}
}
