package org.intermine.webservice.server.lists;

/*
 * Copyright (C) 2002-2012 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.InterMineBag;
import org.intermine.api.profile.Profile;
import org.intermine.webservice.exceptions.BadRequestException;
import org.intermine.webservice.server.Formats;
import org.intermine.webservice.server.WebService;
import org.intermine.webservice.server.core.ListManager;
import org.intermine.webservice.server.output.HTMLTableFormatter;
import org.intermine.webservice.server.output.JSONFormatter;

/**
 * A service to report what lists a user has access to, and some details of
 * those lists.
 * @author Alexis Kalderimis.
 *
 */
public class AvailableListsService extends WebService
{

    /**
     * Constructor
     * @param im A reference to the InterMine API settings bundle
     */
    public AvailableListsService(InterMineAPI im) {
        super(im);
    }

    @Override
    protected void execute() throws Exception {
        Collection<InterMineBag> lists = getLists();
        ListFormatter formatter = getFormatter();
        formatter.setSize(lists.size());
        output.setHeaderAttributes(getHeaderAttributes());
        for (InterMineBag list: lists) {
            if (list == null) {
                continue;
            }
            output.addResultItem(formatter.format(list));
        }
    }

    /**
     * Get the lists for this request.
     * @return The lists that are available.
     */
    protected Collection<InterMineBag> getLists() {
        ListManager listManager = new ListManager(im, getPermission().getProfile());
        return listManager.getLists();
    }

    @Override
    protected int getDefaultFormat() {
        if (hasCallback()) {
            return Formats.JSONP;
        } else {
            return Formats.JSON;
        }
    }

    private Map<String, Object> getHeaderAttributes() {
        Map<String, Object> attributes = new HashMap<String, Object>();
        if (formatIsJSON()) {
            attributes.put(JSONFormatter.KEY_INTRO, "\"lists\":[");
            attributes.put(JSONFormatter.KEY_OUTRO, "]");
        }
        if (formatIsJSONP()) {
            attributes.put(JSONFormatter.KEY_CALLBACK, this.getCallback());
        } if (getFormat() == Formats.HTML) {
            attributes.put(HTMLTableFormatter.KEY_COLUMN_HEADERS,
                Arrays.asList("Name", "Type", "Description", "Size"));
        }
        return attributes;
    }

    private ListFormatter getFormatter() {
        int format = getFormat();
        boolean jsDates = Boolean.parseBoolean(request.getParameter("jsDates"));
        if (formatIsJSON()) { // Most common - test this first.
            Profile profile = getPermission().getProfile();
            return new JSONListFormatter(im, profile, jsDates);
        }
        if (formatIsFlatFile() || Formats.TEXT == format) {
            return new FlatListFormatter(); // One name per line, so tsv and csv is the same
        }
        if (Formats.HTML == format) {
            return new HtmlListFormatter();
        }
        throw new BadRequestException("Unknown request format");
    }

}
