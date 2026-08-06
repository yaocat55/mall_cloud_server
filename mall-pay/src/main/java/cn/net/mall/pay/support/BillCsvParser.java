package cn.net.mall.pay.support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 渠道对账单 CSV 通用解析器.
 *
 * <p>支付宝账单 GBK 编码、微信账单 UTF-8 编码，统一由此工具解析。
 * 每个字段去掉开头的反引号（\`），跳过文件末尾的汇总行。</p>
 *
 * <p>典型用法：
 * <pre>{@code
 *   List<Map<String, String>> rows = BillCsvParser.parse(bytes, StandardCharsets.UTF_8);
 *   for (Map<String, String> row : rows) {
 *       String tradeNo = row.get("商户订单号");
 *       ...
 *   }
 * }</pre>
 */
public final class BillCsvParser {

    private BillCsvParser() {}

    /** 汇总行标识（中文/英文） */
    private static final String SUMMARY_CN = "汇总";
    private static final String SUMMARY_EN = "Total";

    /**
     * 解析 CSV 字节数组，返回行号有序的行列表（每行 key=表头, value=字段值）.
     *
     * <p>第一行为表头，用于构建每行的 key。</p>
     *
     * @param content CSV 字节内容
     * @param charset 文件编码（支付宝 GBK，微信 UTF-8）
     */
    public static List<Map<String, String>> parse(byte[] content, Charset charset) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(content), charset))) {

            // 第一行 = 表头
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                return result;
            }
            String[] headers = splitLine(headerLine);

            // 数据行
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                // 跳过汇总行
                if (isSummaryLine(line)) continue;

                String[] values = splitLine(line);
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i], values[i]);
                }
                // 如果 values 比 headers 长（不太可能但兜底），按索引存
                for (int i = headers.length; i < values.length; i++) {
                    row.put("col_" + i, values[i]);
                }
                result.add(row);
            }
        }
        return result;
    }

    /**
     * 解析 CSV 字节数组，默认 UTF-8 编码.
     */
    public static List<Map<String, String>> parse(byte[] content) throws IOException {
        return parse(content, StandardCharsets.UTF_8);
    }

    // ============ private ============

    /**
     * 分割一行 CSV：以逗号分隔，去除每个字段开头的反引号。
     */
    static String[] splitLine(String line) {
        if (line == null || line.isEmpty()) {
            return new String[0];
        }
        String[] fields = line.split(",", -1);
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            // 去除开头的反引号（渠道 CSV 防 Excel 科学记数的前缀）
            if (field.startsWith("`")) {
                field = field.substring(1);
            }
            // 去除字段前后的空白
            field = field.trim();
            fields[i] = field;
        }
        return fields;
    }

    /**
     * 判断是否为汇总行（文件末尾「汇总笔数/金额」的行）.
     */
    static boolean isSummaryLine(String line) {
        if (line.startsWith("`")) {
            String stripped = line.substring(1).trim();
            return stripped.startsWith(SUMMARY_CN) || stripped.startsWith(SUMMARY_EN);
        }
        return line.trim().startsWith(SUMMARY_CN) || line.trim().startsWith(SUMMARY_EN);
    }
}
