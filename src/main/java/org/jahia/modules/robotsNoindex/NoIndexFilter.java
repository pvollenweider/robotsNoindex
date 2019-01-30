package org.jahia.modules.robotsNoindex;
import java.util.List;

import net.htmlparser.jericho.*;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.cache.AggregateCacheFilter;
import org.slf4j.*;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;



public class NoIndexFilter extends AbstractFilter implements ApplicationListener<ApplicationEvent> {
    private static Logger logger = LoggerFactory.getLogger(NoIndexFilter.class);

    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        String out = previousOut;

        boolean hasNoIndexMixin = resource.getNode().isNodeType("jmix:noindex");

        if (hasNoIndexMixin) {
            logger.debug("jmix:noindex nodeType found for page " + resource.getPath() + ". Add the meta tag on HEAD part");
            String noindexMetaTag = "<meta name=\"robots\" content=\"noindex\">\n";

            Source source = new Source(previousOut);
            OutputDocument outputDocument = new OutputDocument(source);
            if (StringUtils.isNotBlank(noindexMetaTag)) {
                List<Element> headElementList = source.getAllElements(HTMLElementName.HEAD);
                for (Element element : headElementList) {
                    final StartTag headStartTag = element.getStartTag();
                    outputDocument.replace(headStartTag.getBegin()+headStartTag.toString().indexOf(">"), headStartTag.getBegin()+headStartTag.toString().indexOf(">") + 1,
                            ">\n" + AggregateCacheFilter.removeEsiTags(noindexMetaTag) + "\n");
                    break;
                }
            }

            out = outputDocument.toString().trim();
        }
        return out;
    }

    public void onApplicationEvent(ApplicationEvent event) {
    }
}
