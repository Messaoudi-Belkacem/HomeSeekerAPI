package com.example.demo;

import com.example.demo.model.Announcement;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private AnnouncementService announcementService;

	@MockBean
	private AnnouncementRepository announcementRepository;

	@Mock
	private Principal principal;

	@Test
	void contextLoads() {
	}

	@Test
	public void saveAnnouncementTest() throws IOException {
		Announcement announcement = new Announcement(
				4,
				"t",
				150,
				4,
				"Villa",
				"City Center",
				"Batna",
				500000.0,
				"desciption",
				"",
				List.of("name1", "name2", "name3", "name4", "name5", "name6")
				);
		MultipartFile multipartFile = mock(MultipartFile.class);
		List<MultipartFile> multipartFileList = List.of(multipartFile);
		when(announcementRepository.save(announcement)).thenReturn(announcement);
		assertEquals(announcement, announcementService.createAnnouncement(announcement, multipartFileList, principal));
	}
}
