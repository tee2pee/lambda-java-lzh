package lha;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import jp.gr.java_conf.dangan.util.lha.LhaInputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class App implements RequestHandler<RequestParam, String> {

	public String handleRequest(final RequestParam input, final Context context) {
		try {
			Map<String, String> map = new LinkedHashMap<String, String>();
			LhaInputStream lis = null;
			try {
				lis = new LhaInputStream(
						new ByteArrayInputStream(Base64.getDecoder().decode(input.src)));
				LhaHeader hdr;
				while ((hdr = lis.getNextEntry()) != null) {
					ByteArrayOutputStream bos = null;
					try {
						bos = new ByteArrayOutputStream();
						byte[] buf = new byte[256]; int len = 0;
						while ((len = lis.read(buf)) != -1) {
							bos.write(buf, 0, len);
						}
						map.put(hdr.getPath(), Base64.getEncoder().encodeToString(bos.toByteArray()));
					} finally {
						if (bos != null) {
							bos.close();
						}
					}
				}
			} finally {
				if (lis != null) {
					lis.close();
				}
			}
			return this.map2json(map);
		} catch (Throwable e) {
			return "null";
		}
	}

	private String map2json(Map<String, String> map) {
		StringBuilder buf = new StringBuilder();
		buf.append("{");
		int idx = 0;
		for (Map.Entry<String, String> e : map.entrySet()) {
			if (idx++ > 0) {
				buf.append(",");
			}
			buf.append(String.format("\"%s\":\"%s\"", e.getKey(), e.getValue()));
		}
		buf.append("}");
		return buf.toString();
	}
}
