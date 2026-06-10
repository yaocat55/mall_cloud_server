package cn.net.mall.util;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * excel工具类
 *
 * @date 2024/1/23 下午7:42
 */
public abstract class ExcelUtil {

    public static final String TEMP_FILE_PATH = "/tmp/";
    private static final String UTF8 = StandardCharsets.UTF_8.toString();

    private ExcelUtil() {
    }

    /**
     * 导出excel
     *
     * @param fileName 文件名称
     * @param clazz    数据类型
     * @param data     数据
     * @param response 响应
     * @throws IOException 异常
     */
    public static void exportExcel(String fileName, Class clazz, List data, HttpServletResponse response) throws IOException {
        String downloadName = getDownLoadFileName(fileName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding(UTF8);
        response.setHeader("Content-disposition", "attachment;filename=" + downloadName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz)
                .sheet(fileName).doWrite(data);

    }

    private static String getDownLoadFileName(String fileName) throws UnsupportedEncodingException {
        return URLEncoder.encode(fileName + "_" + DateFormatUtil.nowForFile(), UTF8);
    }


    /**
     * 导出excel
     *
     * @param fileName 文件名称
     * @param clazz    数据类型
     * @param data     数据
     * @throws IOException 异常
     */
    public static void exportExcel(String fileName, Class clazz, List data) throws IOException {
        String downloadName = TEMP_FILE_PATH + fileName + ".xlsx";
        File file = new File(downloadName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        EasyExcel.write(fileOutputStream, clazz)
                .sheet(fileName).doWrite(data);
    }
}
