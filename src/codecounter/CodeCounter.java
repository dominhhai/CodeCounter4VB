package codecounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CodeCounter {

	private String fileName;
	private BufferedReader reader;
	private Code code;
	
	public CodeCounter() {
		this(null);
	}
	
	public CodeCounter(String fileName) {
		this.fileName = fileName;
		this.code = new Code();
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;		
	}
	
	public void reset() {
		this.code.reset();
		try {
			this.reader.close();
			this.reader = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Code count() throws IOException {
		this.loadFile();
		this.doCount();
		this.printResult();
		Code code = this.code.clone();
		this.reset();
		
		return code;
	}
	
	private void loadFile() throws FileNotFoundException {
		this.reader = new BufferedReader(new FileReader(this.fileName));
	}
	
	private void doCount() throws IOException {
		int counter = 0;
		String line = null;		
		while ((line = this.reader.readLine()) != null) {
			counter ++;
			char firstChar = this.getFirstCharacter(line);
			// skip empty line
			if (this.isSpace(firstChar)) {
				this.code.empty ++;
			} else {
				if (this.isComment(firstChar)) {
					this.code.comment ++;
				} else if (this.isMarkRegion(firstChar)) {
					this.code.region ++;
				} else {
					this.code.code ++;
				}
			}
		}
		
		this.code.total = counter;
	}
	
	private void printResult() {
		System.out.println("▼------------------- "+ this.fileName +" -----------------▼");
		this.code.print();
		System.out.println("▲------------------- "+ this.fileName +" -----------------▲");
	}
	
	private char getFirstCharacter(String line) {
		char a = ' ';
		int cur = 0;
		int length = line.length();
		
		// skip space characters
		while (cur < length) {			
			a = line.charAt(cur ++);
			if (!this.isSpace(a)) {
				break;
			}
		}
		
		return a;
	}
	
	private boolean isSpace(char chara) {
		return ((chara == ' ') || (chara == '	'));
	}
	
	private boolean isComment(char chara) {
		return chara == '\''; 
	}
	
	private boolean isMarkRegion(char chara) {
		return chara == '#';
	}
	
	private static void travel(File folder, CodeCounter codeCounter, Code total) {
		final String ext = ".vb";
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				travel(file, codeCounter, total);
				continue;
			}
			String fileName = file.getName();
			if (!fileName.substring(fileName.lastIndexOf(".")).equals(ext)) {
				continue;
			}
			try {
				codeCounter.setFileName(file.getAbsolutePath());
				Code code = codeCounter.count();
				total.add(code);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		CodeCounter codeCounter = new CodeCounter();
		Code total = new Code();
		final String path = "C:\\";
		File folder = new File(path);
		travel(folder, codeCounter, total);
		
		System.out.println("▼------------------- "+ path +" -----------------▼");
		total.print();
		System.out.println("▲------------------- "+ path +" -----------------▲");
	}
	
	
	private static class Code {
		public int require;
		public int code;
		public int comment;
		public int region;
		public int empty;
		public int total;
		
		public void reset() {
			this.require = 0;
			this.code = 0;
			this.comment = 0;
			this.region = 0;
			this.empty = 0;
			this.total = 0;
		}
		
		public void add(Code code) {
			this.require += code.require;
			this.code += code.code;
			this.comment += code.comment;
			this.region += code.region;
			this.empty += code.empty;
			this.total += code.total;
		}
		
		public Code clone() {
			Code code = new Code();
			code.require = this.require;
			code.code = this.code;
			code.comment = this.comment;
			code.region = this.region;
			code.empty = this.empty;
			code.total = this.total;
			
			return code;
		}
		
		public void print() {
			System.out.println("comment   : " + this.comment);
			System.out.println("region    : " + this.region + " ~ " + this.region / 2);
			System.out.println("import    : " + this.require);
			System.out.println("real code : " + this.code);
			System.out.println("null line : " + this.empty);
			System.out.println("total line: " + this.total);
		}
	}
}
