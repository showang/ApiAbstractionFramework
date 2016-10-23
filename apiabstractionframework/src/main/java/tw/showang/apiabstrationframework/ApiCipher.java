package tw.showang.apiabstrationframework;

public interface ApiCipher {

	byte[] encode(byte[] source);

	byte[] decode(byte[] source);
}
