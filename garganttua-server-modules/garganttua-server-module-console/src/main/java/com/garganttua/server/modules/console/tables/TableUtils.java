package com.garganttua.server.modules.console.tables;

import java.util.List;

import de.vandermeer.asciitable.AT_Context;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.a7.A7_Grids;

public class TableUtils {

	public static String renderTable(String[] headers, String[][] rows) {
		AT_Context ctx = new AT_Context();
		
		ctx.setGrid( A7_Grids.minusBarPlusEquals() );

		AsciiTable at = new AsciiTable(ctx);
		at.addRule();
		
		//Header

		if( headers != null ) {
			at.addRow(List.of(headers));
			at.addStrongRule();
		}
		
		for( int i = 0; i < rows.length; i++ ) {
			at.addRow(List.of(rows[i]));
			at.addRule();
		}
		
		String rend = at.render();
		return rend;
	}
}
