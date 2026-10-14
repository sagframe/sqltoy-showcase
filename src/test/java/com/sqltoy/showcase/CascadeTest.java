package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.EntityQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.CascadeHead;
import com.sqltoy.showcase.entity.CascadeItem;

/**
 * Head/item cascade through @OneToMany: saving the head persists the items,
 * deleting the head removes them — no per-table code.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class CascadeTest {

	@Autowired
	LightDao lightDao;

	private CascadeHead newHead(String code) {
		CascadeHead head = new CascadeHead().setTransDate(LocalDate.parse("2026-10-01"))
				.setTransCode(code).setTotalCnt(BigDecimal.valueOf(10))
				.setTotalAmt(BigDecimal.valueOf(15000)).setStatus(1);
		head.getItems().add(new CascadeItem().setId(code + "-A").setTransDate(head.getTransDate())
				.setTransId(code).setProductId("P01").setQuantity(BigDecimal.valueOf(6))
				.setPrice(BigDecimal.valueOf(1000)).setAmt(BigDecimal.valueOf(6000)).setStatus(1));
		head.getItems().add(new CascadeItem().setId(code + "-B").setTransDate(head.getTransDate())
				.setTransId(code).setProductId("P02").setQuantity(BigDecimal.valueOf(4))
				.setPrice(BigDecimal.valueOf(1000)).setAmt(BigDecimal.valueOf(4000)).setStatus(1));
		return head;
	}

	private int itemCount(String code) {
		return lightDao.findEntity(CascadeItem.class, EntityQuery.create().where("TRANS_ID=?").values(code))
				.size();
	}

	@Test
	void saveHeadCascadesToItems() {
		lightDao.save(newHead("CAS-01"));
		assertEquals(2, itemCount("CAS-01"));
		System.out.println("cascade save: head persisted with 2 items");
	}

	@Test
	void deleteHeadCascadesToItems() {
		lightDao.save(newHead("CAS-02"));
		assertEquals(2, itemCount("CAS-02"));
		lightDao.delete(newHead("CAS-02").setTransDate(LocalDate.parse("2026-10-01")));
		assertEquals(0, itemCount("CAS-02"));
		System.out.println("cascade delete: items removed together with the head");
	}
}
