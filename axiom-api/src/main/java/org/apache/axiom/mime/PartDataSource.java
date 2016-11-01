/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axiom.mime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.activation.SizeAwareDataSource;

/**
 * Default {@link DataSource} implementation for MIME parts.
 */
final class PartDataSource implements SizeAwareDataSource {
    private final Part part;

    PartDataSource(Part part) {
        this.part = part;
    }

    public String getContentType() {
        return Util.getDataSourceContentType(part);
    }

    public InputStream getInputStream() throws IOException {
        return part.getInputStream(true);
    }

    public String getName() {
        return part.getContentID();
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public long getSize() {
        return part.getBlob().getSize();
    }
}
