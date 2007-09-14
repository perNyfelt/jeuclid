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

package net.sourceforge.jeuclid.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Detects if Batik is in the class path and registers it if its available.
 * 
 * @version $Revision$
 */
public final class BatikDetector {
    /**
     * Logger for this class
     */
    private static final Log LOGGER = LogFactory.getLog(BatikDetector.class);

    private BatikDetector() {
        // Empty on purpose
    }

    /**
     * Detects if Batik is in the classpath.
     * 
     * @param registry
     *            ConverterRegisty to register with.
     */
    public static void detectConversionPlugins(
            final ConverterRegistry registry) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(
                    "org.apache.batik.svggen.SVGGraphics2D");
            BatikDetector.LOGGER.debug("Batik detected!");
            registry.registerMimeTypeAndSuffix(
                    net.sourceforge.jeuclid.converter.Converter.TYPE_SVG,
                    net.sourceforge.jeuclid.converter.Converter.EXTENSION_SVG, true);
            registry.registerConverter(
                    net.sourceforge.jeuclid.converter.Converter.TYPE_SVG,
                    new BatikConverter(), true);
        } catch (final ClassNotFoundException e) {
            BatikDetector.LOGGER.debug("Batik is not in classpath!");
        }
    }

}
