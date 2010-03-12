/*******************************************************************************
 * Copyright (c) 2009, 2010 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 * $Id: $
 *******************************************************************************/
package org.jacoco.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implementation of {@link IMultiReportOutput} that writes files into a
 * {@link ZipOutputStream}.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class ZipMultiReportOutput implements IMultiReportOutput {

	private final ZipOutputStream zip;

	private OutputStream currentEntry;

	/**
	 * Creates a new instance based on the given {@link ZipOutputStream}.
	 * 
	 * @param zip
	 *            stream to write file entries to
	 */
	public ZipMultiReportOutput(final ZipOutputStream zip) {
		this.zip = zip;
	}

	public OutputStream createFile(final String path) throws IOException {
		if (currentEntry != null) {
			currentEntry.close();
		}
		final ZipEntry entry = new ZipEntry(path);
		zip.putNextEntry(entry);
		currentEntry = new EntryOutput();
		return currentEntry;
	}

	private final class EntryOutput extends OutputStream {

		private boolean closed = false;

		@Override
		public void write(final byte[] b, final int off, final int len)
				throws IOException {
			ensureNotClosed();
			zip.write(b, off, len);
		}

		@Override
		public void write(final byte[] b) throws IOException {
			ensureNotClosed();
			zip.write(b);
		}

		@Override
		public void write(final int b) throws IOException {
			ensureNotClosed();
			zip.write(b);
		}

		@Override
		public void flush() throws IOException {
			ensureNotClosed();
			zip.flush();
		}

		@Override
		public void close() throws IOException {
			if (!closed) {
				closed = true;
				zip.closeEntry();
			}
		}

		private void ensureNotClosed() throws IOException {
			if (closed) {
				throw new IOException("Zip entry already closed.");
			}
		}

	}

}