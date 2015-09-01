
public class Authenticator {

	private String id;
	private long timeStamp;

	public Authenticator(String str){
		parseIt(str);
	}
	public Authenticator(String id, long timeStamp){
		this.id=id;
		this.timeStamp=timeStamp;

	}

	private void parseIt(String str){
		int currentChar=0;

		while(str.charAt(currentChar)!=',' ){
			currentChar++;
		}
		//System.out.println(tokenBeginChar+" "+currentChar);
		this.id=new String(str.substring(0, currentChar));
		//System.out.println(str+" "+currentChar+" "+str.length());
		this.timeStamp=Long.valueOf(str.substring(currentChar+1, str.length()));

	}


	public String getId(){
		return this.id;
	}

	public long getTimeStamp(){
		return this.timeStamp;
	}

	public String toString(){
		return this.id+","+this.timeStamp;
	}



}


