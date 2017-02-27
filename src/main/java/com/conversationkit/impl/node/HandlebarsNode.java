/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.conversationkit.impl.node;

import com.conversationkit.impl.node.ConversationNode;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.conversationkit.model.IConversationState;
import com.conversationkit.model.SnippetContentType;
import com.conversationkit.model.SnippetType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * An implementation of <code>IConversationNode</code> backed by the
 * Handlebars template engine. You must include the Handlebars jars on
 * your classpath to use this node type.
 * 
 * @author pdtyreus
 * @see <a href="http://jknack.github.io/handlebars.java/">Handlebars Java</a>
 */
public class HandlebarsNode<S extends IConversationState> extends ConversationNode<S> {

    private static final Logger logger = Logger.getLogger(HandlebarsNode.class.getName());

    protected final String suggestions;
    protected final String content;
    protected final SnippetContentType contentType;
    protected final Handlebars handlebars = new Handlebars();

    public HandlebarsNode(int id, SnippetType type, String content, SnippetContentType contentType) {
        super(id, type);
        this.suggestions = null;
        this.content = content;
        this.contentType = contentType;
    }

    public HandlebarsNode(int id, SnippetType type, String content, String suggestions, SnippetContentType contentType) {
        super(id, type);
        this.suggestions = suggestions;
        this.content = content;
        this.contentType = contentType;
    }

    @Override
    public String renderContent(S state) {
        try {
            Template template = handlebars.compileInline(this.content);
            return template.apply(state);
        } catch (IOException e) {
            logger.warning(e.getMessage());
            return this.content;
        }
    }

    @Override
    public SnippetContentType getContentType() {
        return this.contentType;
    }

    @Override
    public Iterable<String> getSuggestedResponses(S state) {
        if (suggestions == null) {
            return null;
        }
        try {
            Template template = handlebars.compileInline(suggestions);
            String responseList = template.apply(state);
            String[] lines = responseList.split("\\|");
            List<String> suggestedResponses = new ArrayList();
            for (String line : lines) {
                if (!line.isEmpty()) {
                    suggestedResponses.add(line);
                }
            }
            return suggestedResponses;
        } catch (IOException e) {
            logger.warning(e.getMessage());
            return null;
        }
    }

}
