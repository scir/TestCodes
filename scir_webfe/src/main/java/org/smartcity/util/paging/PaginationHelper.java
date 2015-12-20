package org.smartcity.util.paging;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

public class PaginationHelper<E> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Page<E> fetchPage(
            final JdbcTemplate jt,
            final String sqlCountRows,
            final String sqlFetchRows,
            final Object args[],
            final Integer pageNo,
            final Integer pageSize,
            final RowMapper<E> rowMapper) {

        // determine how many rows are available
    	final Integer rowCount = jt.queryForObject(sqlCountRows, Integer.class);

        // calculate the number of pages
        Integer pageCount = rowCount / pageSize;
        if (rowCount > pageSize * pageCount) {
            pageCount++;
        }

        // create the page object
        final Page<E> page = new Page<E>();
        page.setPageSize(pageSize);
        page.setPageNumber(pageNo);
        page.setPagesAvailable(pageCount);
        page.setRowCount(rowCount);

        // fetch a single page of results
        final Integer startRow = (pageNo - 1) * pageSize;
        
        jt.query(
                sqlFetchRows,
                args,
                new ResultSetExtractor() {

					@Override
					public Object extractData(java.sql.ResultSet rs) throws SQLException, DataAccessException {
                        final List pageItems = page.getPageItems();
                        Integer currentRow = 0;
                        while (rs.next() && currentRow < startRow + pageSize) {
                            if (currentRow >= startRow) {
                                pageItems.add(rowMapper.mapRow(rs, currentRow));
                            }
                            currentRow++;
                        }
                        return page;
					}
					
                });
        
//        final int rowCount = jt.getMaxRows();
//        
//        // calculate the number of pages
//        int pageCount = rowCount / pageSize;
//        if (rowCount > pageSize * pageCount) {
//            pageCount++;
//        }
//
//        page.setPageNumber(pageNo);
//        page.setPagesAvailable(pageCount);
//        page.setRowCount(rowCount);
        
        return page;
    }

}