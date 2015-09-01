
public class Ticket {

	private String clientId;
	private String clientIp;
	private long ticketValidityPeriod;
	private String client_tgsSessionKey;
	
	/*
	public static void main(String[] args){
		TgsTicket t= new TgsTicket("a,v,sees,w");
		System.out.println(t);
	}
	*/
	public Ticket(String ticket){
		parseIt(ticket);
	}
	public Ticket(String clientId, String clientIp, long ticketValidityPeriod, String client_tgsSessionKey){
		this.clientId=clientId;
		this.clientIp=clientIp;
		this.ticketValidityPeriod=ticketValidityPeriod;
		this.client_tgsSessionKey=client_tgsSessionKey;
	}
	
	private void parseIt(String str){
		int tokenCounter=0;
		int currentChar=0;
		int tokenBeginChar=0;
		
		while(currentChar<str.length()){
			String token="";
			while(currentChar< str.length() && str.charAt(currentChar)!=','  ){
				currentChar++;
			}
			//System.out.println(tokenBeginChar+" "+currentChar);
			
			if(tokenCounter==0){
				this.clientId=new String(str.substring(tokenBeginChar, currentChar));
			}else if(tokenCounter==1){
				this.clientIp=new String(str.substring(tokenBeginChar, currentChar));
			}else if (tokenCounter==2){
				this.ticketValidityPeriod=Long.valueOf(str.substring(tokenBeginChar, currentChar));
			}else if (tokenCounter==3){
				this.client_tgsSessionKey=new String(str.substring(tokenBeginChar, str.length()));
			}
			currentChar++;
			tokenCounter++;
			tokenBeginChar=currentChar;
		}
	}
	
	public String getClientId(){
		return this.clientId;
		
	}
	
	public String getClientIp(){
		return this.clientIp;
		
	}
	
	public long getTicketValidityPeriod(){
		return this.ticketValidityPeriod;
		
	}
	
	public String getClientSessionKey(){
		return this.client_tgsSessionKey;
	}
	
	public String toString(){
		return clientId+","+clientIp+","+ticketValidityPeriod+","+client_tgsSessionKey;
	}
}
