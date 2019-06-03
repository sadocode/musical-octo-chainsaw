package file;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileRead{
	private File file;
	private FileInputStream fis;
	private byte[] fileData;

	public FileRead(File f){
		this.file = f;
		this.fileData = new byte[(int)f.length()];
	}
	/**
	 * read하는 file의 길이를 리턴한다.
	 * @return file.length()
	 */
	public int getFileLength() {
		return (int)this.file.length();
	}
	
	/**
	 * fread() 가 수행된 이후에 정상적인 값이 리턴된다.
	 * 
	 * @return
	 */
	public byte[] getFileData(){
		return this.fileData;
	}
	/**
	 * FileInputStream을 통해 읽은 파일을 byte[] fileData에 저장한다.
	 * @throws IOException
	 */
	public void fread() throws IOException{
		try{
			this.fis = new FileInputStream(this.file);
			this.fis.read(this.fileData);
		} catch(Exception e){
			e.printStackTrace(System.out);
		} finally{
			if(this.fis != null)
				this.fis.close();
			this.fis = null;
		}
	}
}