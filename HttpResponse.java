public class HttpResponse {
    int statusCode;
    String reasonPhrase;
    MimeHeader mh;

    public HttpResponse(String response) {
    	int firstLastChar = response.indexOf("\r\n");
    	int statusCounter1 = response.indexOf("\r");
    	int statusCounter2 = response.indexOf("OK",statusCounter1);
    	String responseFirstLine = response.substring(1,firstLastChar);
    	statusCode = Integer.parseInt((response.substring(statusCounter1, statusCounter2)));
        reasonPhrase = response.substring(13,15);
        String raw_mime_header = response;
        mh = new MimeHeader(raw_mime_header);
    }

    public HttpResponse(int code, String reason, MimeHeader m) {
        statusCode = code;
        reasonPhrase = reason;
        mh = m;
        mh.put("Connection", "close");
    }

    public String toString() {
        return "HTTP/1.1 " + statusCode + " " + reasonPhrase + "\r\n" + mh + "\r\n";
    }
}
