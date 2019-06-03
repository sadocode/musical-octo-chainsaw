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
	 * read�ϴ� file�� ���̸� �����Ѵ�.
	 * @return file.length()
	 */
	public int getFileLength() {
		return (int)this.file.length();
	}
	
	/**
	 * fread() �� ����� ���Ŀ� �������� ���� ���ϵȴ�.
	 * 
	 * @return
	 */
	public byte[] getFileData(){
		return this.fileData;
	}
	/**
	 * FileInputStream�� ���� ���� ������ byte[] fileData�� �����Ѵ�.
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