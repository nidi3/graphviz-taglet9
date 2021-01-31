/*
 * Copyright © 2019 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.taglet9;

import com.sun.source.doctree.DocTree;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import jdk.javadoc.doclet.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Support graphviz inside javadoc.
 * <p>
 * {@graphviz
 * graph test {
 * rankdir=LR
 * a -- b
 * b -- c [color=red]
 * }
 * }
 * </p>
 * end
 */
public class GraphvizTaglet implements Taglet {
    private static final Pattern FULL_PATTERN = Pattern.compile("\\s*\\{\\s*@graphviz\\s*(.*?)}\\s*$", Pattern.DOTALL);
    private static final Pattern START_PATTERN = Pattern.compile("^\\s*(di)?graph\\s*(.*?)\\s\\{");

    private Map<String, Integer> packageCount = new HashMap<>();
    private Map<Element, String> images = new HashMap<>();

    @Override
    public Set<Location> getAllowedLocations() {
        return new HashSet<>(Arrays.asList(Location.values()));
    }

    /**
     * {@graphviz
     * graph test {
     * b -- c [color=red]
     * }
     * }
     */
    @Override
    public boolean isInlineTag() {
        return true;
    }

    @Override
    public String getName() {
        return "graphviz";
    }

    @Override
    public void init(DocletEnvironment env, Doclet doclet) {
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {
        try {
            final DocTree tag = tags.get(0);
            String image = images.get(element);
            if (image == null) {
                final File file = new File(packageOf(element), imageNameOf(element));
                image = Graphviz.fromString(dotOf(tag)).render(Format.PNG).toFile(file).getName();
                images.put(element, image);
            }
            return "<img title='" + imageTitleOf(tag) + "' src='" + image + "'></img>";
        } catch (IOException e) {
            throw new RuntimeException("Problem writing graphviz file", e);
        }
    }

    private String dotOf(DocTree tag) {
        final Matcher matcher = FULL_PATTERN.matcher(tag.toString());
        if (!matcher.find()) {
            throw new IllegalArgumentException("Graphviz tag had not the expected format: '" + tag.toString() + "'");
        }
        return matcher.group(1);
    }

    private String imageTitleOf(DocTree tag) {
        final Matcher matcher = START_PATTERN.matcher(dotOf(tag));
        return matcher.find() ? matcher.group(2) : "";
    }

    private String imageNameOf(Element e) {
        return "graphviz" + packageCount.merge(packageOf(e), 1, Integer::sum);
    }

    private String packageOf(Element e) {
        while (e != null && e.getKind() != ElementKind.PACKAGE) {
            e = e.getEnclosingElement();
        }
        return e == null ? "" : e.toString().replace('.', '/');
    }
}
