package com.cgnb;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HttpUtil {

	public static String ENCODE = "UTF-8";

//	public static String getHostIpV4String(final String host)
//			throws UnsupportedEncodingException, UnknownHostException {
//		// "zhidao.baidu.com"
//		Pattern ipv4 = Pattern.compile(DnsJava.IPv4_REGEX);
//		Matcher matcher = ipv4.matcher(host);
//		if (matcher.matches()) {
//			return host;
//		}
//		InetAddress src = InetAddress.getByName(host);
//		return IntegerTool.byteArrayToIpV4String(src.getAddress());
//	}

	public static String HEAD_VALUES_SPLIT = ",";

	public static String getFirstHeadString(
			final Map<String, List<String>> heads, final String key) {
		if (null != key && null != heads && heads.size() > 0) {
			List<String> values = heads.get(key);
			if (null != values && values.size() > 0) {
				return values.get(0);
			}
		}
		return null;
	}

	public static String getFirstHeadStringIgnoreCase(
			final Map<String, List<String>> heads, final String key) {
		if (null != key && null != heads && heads.size() > 0) {
			for (Entry<String, List<String>> entry : heads.entrySet()) {
				String key1 = entry.getKey();
				if (key.equalsIgnoreCase(key1)) {
					List<String> values = entry.getValue();
					if (null != values && values.size() > 0) {
						return values.get(0);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 指定协议,服务器名称/ip,端口,uri,拼接URL
	 * 
	 * @param protocol
	 * @param server
	 * @param port
	 * @param uri
	 * @return
	 */
	public static String getURL(final String protocol, final String server,
			final int port, String uri) {
		if (uri == null || uri.length() == 0) {
			uri = "/";
		}
		if (!uri.startsWith("/")) {
			uri = "/" + uri;
		}
		boolean appendPort = true;
		if (("http".equals(protocol) && port == 80)
				|| ("https".equals(protocol) && port == 443)
				|| ("ftp".equals(protocol) && port == 21)) {
			appendPort = false;
		}
		if (appendPort) {
			return protocol + "://" + server + ":" + port + uri;
		}
		return protocol + "://" + server + uri;
	}

	public static String getURL(final boolean isHttps, final String server,
			final int port, String uri) {
		return getURL(isHttps ? "https" : "http", server, port, uri);
	}

	public static String getURL(final String server, final int port, String uri) {
		return getURL(false, server, port, uri);
	}

	public static String getURL(final String server, String uri) {
		return getURL(false, server, 80, uri);
	}

	public static String format(final Map<String, String> values,
			final String charset) {
		if (null == values)
			return "";
		List<String[]> paras = new ArrayList<String[]>();
		for (Entry<String, String> entries : values.entrySet()) {
			paras.add(new String[] { entries.getKey(), entries.getValue() });
		}
		return format(paras, charset);
	}

	public static String format(final List<String[]> parameters,
			final String charset) {
		return format(parameters, '&', charset);
	}

	public static String format(final List<String[]> parameters,
			final char parameterSeparator, final String charset) {
		StringBuilder result = new StringBuilder();
		Iterator<String[]> i$ = parameters.iterator();
		do {
			if (!i$.hasNext())
				break;
			String[] parameter = i$.next();
			try {
				String encodedName = encodeFormField(parameter[0], charset);
				if (encodedName != null && encodedName.length() > 0) {
					String encodedValue = encodeFormField(parameter[1], charset);
					if (result.length() > 0)
						result.append(parameterSeparator);
					result.append(encodedName);
					if (encodedValue != null) {
						result.append("=");
						result.append(encodedValue);
					}
				}
			} catch (UnsupportedEncodingException e) {
				//e.printStackTrace();
			}
		} while (true);
		return result.toString();
	}

	private static String encodeFormField(final String content,
			final String charset) throws UnsupportedEncodingException {
		if (content == null)
			return "";
		else
			return URLEncoder.encode(content, charset);
	}

	private static String encodeFormFields(final String content,
			final String charset) {
		if (content == null)
			return null;
		else
			return urlEncode(content, charset == null ? Charset.forName(ENCODE)
					: Charset.forName(charset), null, true);
	}

	private static String urlEncode(final String content,
			final Charset charset, final BitSet safechars,
			final boolean blankAsPlus) {
		if (content == null)
			return null;
		StringBuilder buf = new StringBuilder();
		for (ByteBuffer bb = charset.encode(content); bb.hasRemaining();) {
			int b = bb.get() & 255;
			if (safechars.get(b))
				buf.append((char) b);
			else if (blankAsPlus && b == 32) {
				buf.append('+');
			} else {
				buf.append("%");
				char hex1 = Character.toUpperCase(Character.forDigit(
						b >> 4 & 15, 16));
				char hex2 = Character.toUpperCase(Character
						.forDigit(b & 15, 16));
				buf.append(hex1);
				buf.append(hex2);
			}
		}

		return buf.toString();
	}

}
