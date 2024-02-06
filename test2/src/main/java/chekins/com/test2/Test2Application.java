package chekins.com.test2;

import chekins.com.test2.model.Location;
import chekins.com.test2.repository.LocationRepository;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
@EnableRedisDocumentRepositories(basePackages = "chekins.com.test2.*")
public class Test2Application {
	@Autowired
	LocationRepository locationRepository;

	@Value("${DUMP_FILE_PATH}")
	private String dumpFilePath;
	@Bean
	CommandLineRunner loadDataReader(@Value("${XLSX_FILE_PATH}") String dataResource) throws IOException{
		return args -> {
			locationRepository.deleteAll();

			try {
				File fileToDelete = new File(dumpFilePath);
				if (fileToDelete.exists()) {
					if (fileToDelete.delete()) {
						System.out.println("File '" + dumpFilePath + "' deleted successfully.");
					} else {
						System.err.println("Failed to delete file '" + dumpFilePath + "'.");
					}
				} else {
					System.out.println("File '" + dumpFilePath + "' does not exist.");
				}
			} catch (Exception e) {
				System.err.println("Error deleting file: " + e.getMessage());
			}
			XSSFReaderExample example = new XSSFReaderExample();
			List<Location> fetchedLocations =  example.readExcelFile(dataResource);

			locationRepository.saveAll(fetchedLocations);
		};
	}


	public static void main(String[] args) {
		SpringApplication.run(Test2Application.class, args);
	}

}
