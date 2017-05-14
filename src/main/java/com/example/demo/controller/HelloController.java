package com.example.demo.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

@Controller
public class HelloController {
	
	@Autowired
	ServletContext servletContext;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("name", "Marco");
		return "hello";
	}
	
	@RequestMapping(value = "/invoice", method = RequestMethod.GET)
	public String pdf(Model model, HttpServletRequest request, @RequestParam("format") String format) {
		String name = request.getParameter("name");
		Properties headers = new Properties();
		headers.put("ignoreGraphics", "false");
	     
		model.addAttribute("datasource", new ArrayList<>());
		model.addAttribute("format", format);
		model.addAttribute("exporterParameters", headers);
		
		model.addAttribute("COMPANY_NAME", name);
		model.addAttribute("BASE_DIR", servletContext.getRealPath("/WEB-INF/classes/reports/"));
		return "rpt_invoice";
	}
	
	@RequestMapping(value = "/reportxls", method = RequestMethod.GET)
	@ResponseBody
	public void xls(HttpServletRequest request, HttpServletResponse response) {
		try {
			String name = request.getParameter("name");
			
			InputStream jasperStream = this.getClass().getResourceAsStream("/reports/rpt_invoice.jasper");
			Map<String,Object> params = new HashMap<>();
			params.put(JRParameter.REPORT_LOCALE, Locale.US);
			params.put("COMPANY_NAME", name);
			params.put("BASE_DIR", servletContext.getRealPath("/WEB-INF/classes/reports/"));

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(new ArrayList<>()));

		    OutputStream outStream = response.getOutputStream();

		    response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=situacionAbastecimientoReporteXls.xls");

		    JRXlsExporter exporter = new JRXlsExporter();
		    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
		    
		    SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
		    configuration.setCollapseRowSpan(false);
		    configuration.setIgnoreCellBorder(false);
		    configuration.setIgnoreCellBackground(false);
		    configuration.setIgnoreGraphics(false);
		    configuration.setImageBorderFixEnabled(false);
		    configuration.setWrapText(false);
		    
//		    configuration.setDetectCellType(true);
//		    configuration.setOnePagePerSheet(false);
//		    configuration.setRemoveEmptySpaceBetweenRows(false);
//		    configuration.setRemoveEmptySpaceBetweenColumns(false);
//		    configuration.setWhitePageBackground(false);
//		    configuration.setIgnoreGraphics(false);
//		    configuration.setIgnoreCellBorder(false);
//		    configuration.setFontSizeFixEnabled(true);
//		    configuration.setMaxRowsPerSheet(0);
		    
		    exporter.setConfiguration(configuration);
		    exporter.exportReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
