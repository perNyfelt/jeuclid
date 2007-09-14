/*
 * Copyright 2007 - 2007 JEuclid, http://jeuclid.sf.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

/*
 * Please note: This file was originally taken from the Apache FOP project,
 * available at http://xmlgraphics.apache.org/fop/ It is therefore
 * partially copyright (c) 1999-2007 The Apache Software Foundation.
 * 
 * Parts of the contents are heavily inspired by work done for Barcode4J by
 * Jeremias Maerki, available at http://barcode4j.sf.net/
 */

package net.sourceforge.jeuclid.fop;

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.jeuclid.MathMLParserSupport;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.image.FopImage;
import org.apache.fop.image.analyser.XMLReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Reader plugin for the ODF Format.
 * 
 * @version $Revision$
 */
public class ODFReader extends XMLReader {

    /**
     * Default constructor.
     */
    public ODFReader() {
    }

    /** {@inheritDoc} */
    @Override
    protected FopImage.ImageInfo loadImage(final String uri,
            final InputStream bis, final FOUserAgent ua) {

        Document doc;
        try {
            doc = MathMLParserSupport.parseInputStreamODF(bis);
            return new JEuclidElementMapping.MathMLConverter().convert(doc);
        } catch (final SAXException e) {
            // ignore
        } catch (final IOException e) {
            // ignore
        }
        return null;
    }

}