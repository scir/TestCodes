package org.smartcity.util.paging;

import java.util.ArrayList;
import java.util.List;

public class Page<E> {

	private Integer pageNumber;
	private Integer pageSize ;
	private Integer pagesAvailable;
	private Integer rowCount;
	private List<E> pageItems = new ArrayList<E>();

	public Page() {}
	public Page(List<E> pageItems){	this.pageItems=pageItems; }
	
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setPagesAvailable(Integer pagesAvailable) {
		this.pagesAvailable = pagesAvailable;
	}

	public void setPageItems(List<E> pageItems) {
		this.pageItems = pageItems;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPagesAvailable() {
		return pagesAvailable;
	}

	public List<E> getPageItems() {
		return pageItems;
	}

	public Integer getRowCount() {
		return rowCount;
	}
	
	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}
}
