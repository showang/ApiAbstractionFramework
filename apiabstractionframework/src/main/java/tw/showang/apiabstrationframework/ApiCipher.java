package tw.showang.apiabstrationframework;

import tw.showang.apiabstrationframework.error.ApiCipherException;

public interface ApiCipher {

	byte[] encode(byte[] source) throws ApiCipherException;

	byte[] decode(byte[] source) throws ApiCipherException;
}
