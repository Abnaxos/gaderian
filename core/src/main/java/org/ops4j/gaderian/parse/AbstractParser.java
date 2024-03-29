// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ops4j.gaderian.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Resource;
import org.ops4j.gaderian.impl.LocationImpl;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Abstract super-class for parsers based around the SAX event model. This class provides support
 * for managing a stack of elements, making it reasonable to establish relationships between
 * elements. It also assists in setting the {@link org.ops4j.gaderian.Location} of elements as they
 * are created.
 * <p>
 * This support is structured around both XML but is suited towards configuration files rather than
 * documents, in that the <em>content</em> (parsable character data) within an element is
 * concatinated together and tracked as a single blob.
 *
 * @author Howard Lewis Ship
 */
public abstract class AbstractParser extends DefaultHandler
{

    /**
     * The parser is built around a stack of these Items. This used to figure out the current state,
     * the element being processed, and the matching descriptor object.
     */
    private static class Item
    {
        StringBuffer _buffer;

        String _elementName;

        boolean _ignoreCharacterData;

        Object _object;

        /**
         * Prior state of the parser before this item was pushed.
         */
        DescriptorParsingState _priorState;

        Item(String elementName, Object object, DescriptorParsingState priorState, boolean ignoreCharacterData)
        {
            _elementName = elementName;
            _object = object;
            _priorState = priorState;
            _ignoreCharacterData = ignoreCharacterData;
        }

        void addContent(char[] buffer, int start, int length)
        {
            if (_ignoreCharacterData)
                return;

            if (_buffer == null)
                _buffer = new StringBuffer(length);

            _buffer.append(buffer, start, length);
        }

        String getContent()
        {
            if (_buffer != null)
                return _buffer.toString().trim();

            return null;
        }
    }

    private int _currentColumn;

    private int _currentLine;

    private Location _location;

    private Locator _locator;

    private Resource _resource;

    private List<Item> _stack;

    private DescriptorParsingState _state;

    private Item _top;

    /**
     * Accepts parseable character data from within an element and applies it to the top stack
     * element. This may be invoked multiple times by the parser, and the overall data will
     * accumulate. This content can be retrieved via {@link #peekContent()}.
     */
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        _top.addContent(ch, start, length);
    }

    /**
     * Invokes {@link #fatalError(SAXParseException)}.
     */
    public void error(SAXParseException ex) throws SAXException
    {
        fatalError(ex);
    }

    /**
     * @param ex
     *            exception to be thrown
     * @throws SAXParseException
     */
    public void fatalError(SAXParseException ex) throws SAXException
    {
        throw ex;
    }

    /**
     * Returns a "path" to the current element, as a series of element names seperated by slashes,
     * i.e., "top/middle/leaf".
     */
    protected String getElementPath()
    {
        StringBuffer buffer = new StringBuffer();

        int count = _stack.size();
        for (int i = 0; i < count; i++)
        {
            if (i > 0)
                buffer.append('/');

            Item item = (Item) _stack.get(i);

            buffer.append(item._elementName);
        }

        return buffer.toString();
    }

    /**
     * Returns the current lcoation, as reported by the parser.
     */
    protected Location getLocation()
    {
        // If for some reason the _locator has not yet been initilized - empty file for example (GAD-12)
        if (_locator == null)
        {
            return null;
        }

        int line = _locator.getLineNumber();
        int column = _locator.getColumnNumber();

        if (line != _currentLine || column != _currentColumn)
            _location = null;

        if (_location == null)
            _location = new LocationImpl(_resource, line, column);

        _currentLine = line;
        _currentColumn = column;

        return _location;
    }

    /**
     * Returns the {@link Resource} being parsed (as set by {@link #initializeParser(Resource, DescriptorParsingState)}.
     */

    protected Resource getResource()
    {
        return _resource;
    }

    /**
     * Returns the current state of the parser. State is initially set by
     * {@link #initializeParser(Resource, DescriptorParsingState)} and is later updated by
     * {@link #push(String, Object, DescriptorParsingState, boolean)} and {@link #pop()}.
     */
    protected DescriptorParsingState getState()
    {
        return _state;
    }

    /**
     * Initializes the parser; this should be called before any SAX parse events are received.
     *
     * @param resource
     *            the resource being parsed (used for some error messages)
     * @param startState
     *            the initial state of the parser (the interpretation of state is determined by
     *            subclasses)
     */
    protected void initializeParser(Resource resource, DescriptorParsingState startState)
    {
        _resource = resource;
        _stack = new ArrayList<Item>();

        _location = null;
        _state = startState;
    }

    /**
     * Peeks at the top element on the stack, and returns its content (the accumuulated parseable
     * character data directly enclosed by its start/end tags.
     */
    protected String peekContent()
    {
        return _top.getContent();
    }

    /**
     * Peeks at the top element on the stack and returns its element name.
     */
    protected String peekElementName()
    {
        return _top._elementName;
    }

    /**
     * Peeks at the top element on the stack and returns the object for that element.
     */

    protected Object peekObject()
    {
        return _top._object;
    }

    /**
     * Invoked when the closing tag for an element is enountered {i.e, from
     * {@link #endElement(String, String, String)}. This removes the corresponding item from the
     * stack, and sets the parser state back to the (new) top element's state.
     */
    protected void pop()
    {
        int count = _stack.size();

        _state = _top._priorState;

        _stack.remove(count - 1);

        if (count == 1)
            _top = null;
        else
            _top = (Item) _stack.get(count - 2);
    }

    /**
     * Enters a new state, pushing an object onto the stack. Invokes
     * {@link #push(String, Object, DescriptorParsingState, boolean)}, and ignores character data within the element.
     *
     * @param elementName
     *            the element whose start tag was just parsed
     * @param object
     *            the object created to represent the new object
     * @param state
     *            the new state for the parse
     */
    protected void push(String elementName, Object object, DescriptorParsingState state)
    {
        push(elementName, object, state, true);
    }

    /**
     * Enters a new state, pusubhing an object onto the stack. If the object implements
     * {@link org.ops4j.gaderian.LocationHolder} then its location property is set to the
     * current location.
     *
     * @param elementName
     *            the element whose start tag was just parsed
     * @param object
     *            the object created to represent the new object
     * @param state
     *            the new state for the parse
     * @param ignoreCharacterData
     *            if true, then any character data (typically whitespace) directly enclosed by the
     *            element is ignored
     */
    protected void push(String elementName, Object object, DescriptorParsingState state, boolean ignoreCharacterData)
    {
        Gaderian.setLocation(object, getLocation());

        Item item = new Item(elementName, object, _state, ignoreCharacterData);

        _stack.add(item);

        _top = item;
        _state = state;
    }

    /**
     * Resets all state after a parse.
     */
    protected void resetParser()
    {
        _resource = null;
        _locator = null;
        _stack = null;
        _location = null;
    }

    /**
     * Invoked by the parser, the locator is stored and later used by {@link #getLocation()}.
     */
    public void setDocumentLocator(Locator locator)
    {
        _locator = locator;
    }

    /**
     * Forces a change to a specific state.
     */
    protected void setState(DescriptorParsingState state)
    {
        _state = state;
    }

    /**
     * Invoked when an unexpected element is parsed (useful for parses that don't perform
     * validation, or when there's no DTD).
     *
     * @throws ApplicationRuntimeException
     *             describing the situation
     */
    protected void unexpectedElement(String elementName)
    {
        throw new ApplicationRuntimeException(ParseMessages.unexpectedElement(
                elementName,
                getElementPath()), getLocation(), null);
    }

    /**
     * Ocassionaly it is necessary to "change our mind" about what's on the top of the stack.
     *
     * @param object
     *            the new object for the top stack element
     */
    protected void updateObject(Object object)
    {
        _top._object = object;
    }

    /**
     * Invokes {@link #fatalError(SAXParseException)}.
     */
    public void warning(SAXParseException ex) throws SAXException
    {
        fatalError(ex);
    }

    private Map<String,String> constructAttributesMap(Attributes attributes)
    {
        Map<String,String> result = new HashMap<String,String>();
        int count = attributes.getLength();

        for (int i = 0; i < count; i++)
        {
            // We need to ignore some attributes when using schemas (xsds)
            // in combination with Gaderian descriptors
            // Currently the following attributes are ignored:
            // - xsi:type
            if (shouldIgnore(attributes,i))
            {
                continue;
            }

            String key = attributes.getLocalName(i);

            if (Gaderian.isBlank(key))
                key = attributes.getQName(i);

            String value = attributes.getValue(i);

            result.put(key, value);
        }

        return result;
    }

    /** Chechs whether the attribute at index i should be ignored or not
     *  Currently the following attributes are ignored:
     * - xsi:type
     *
     * @param attributes The list of attributes to check
     * @param i The current index
     * @return True if the attributes should be ignored, otherwise false
     */
    private boolean shouldIgnore( final Attributes attributes, final int i )
    {
        // Ignore the xsi:type definitions as they are (currently) not used by the parser
        // But, by ignoring these, you can use the schema (xsd) for the
        // module descriptors and not get errors during parsing

        final String qName = attributes.getQName( i );
        return Gaderian.isNonBlank( qName ) && "xsi:type".equals( qName );

    }

    /**
     * Invoked when an element's start tag is recognized. The element and attributes are provided to
     * the subclass for further processing.
     */
    protected abstract void begin(String elementName, Map<String,String> attributes);

    /**
     * Invoked when an element's close tag is recognized. The element is provided. The content of
     * the element (the unparsed whitespace within the element's tags) is available via
     * {@link #peekContent()}.
     */
    protected abstract void end(String elementName);

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        end(localName);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException
    {
        begin(localName, constructAttributesMap(attributes));
    }

}