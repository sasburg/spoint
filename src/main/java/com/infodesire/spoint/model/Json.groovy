// (C) 1998-2016 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.spoint.model;

import com.infodesire.spoint.base.SpointCode
import com.infodesire.spoint.base.SpointException

import groovy.json.JsonException
import groovy.json.JsonSlurper

import java.text.DecimalFormat
import java.text.SimpleDateFormat


/**
 * Parse and format model objects in JSON
 *
 */
public class Json {

  public static List<SPList> parseLists( String content ) throws SpointException {
    
    try {
      def json = new JsonSlurper().parseText( content );
      json['d']['results'].collect { list ->
        return createList( list );
      }
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }

  public static SPList parseList( String content ) {
    
    try {
      def json = new JsonSlurper().parseText( content );
      def list = json['d']
      checkFound( list );
      return createList( list );
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }
  
  public static List<SPListItem> parseListItems( String content ) {
    
    try {
      def json = new JsonSlurper().parseText( content );
      json['d']['results'].collect { listItem ->
        return createListItem( listItem );
      }
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }
  
  
  public static List<SPFolder> parseFolders( String content ) throws SpointException {
    
    try {
      def json = new JsonSlurper().parseText( content );
      json['d']['results'].collect { folder ->
        return createFolder( folder );
      }
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }
  
  
  public static SPFolder parseFolder( String content ) throws SpointException {
    
    try {
      def json = new JsonSlurper().parseText( content );
      def folder = json['d'];
      checkFound( folder );
      return createFolder( folder );
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }
  
  
  private static SPList createList( Object list ) {
    def metadata = list['__metadata'];
    return new SPList(
      id: list['Id'],
      uri: metadata['uri'],
      title: list['Title'],
      description: list['Description']
    );
  }
  

  private static SPListItem createListItem( Object listItem ) {
    def metadata = listItem['__metadata'];
    return new SPListItem(
      guid: listItem['GUID'],
      uri: metadata['uri'],
      title: listItem['Title'],
      type: metadata['type']
    );
  }
  
  
  private static SPFolder createFolder( Object folder ) {
    def metadata = folder['__metadata'];
    return new SPFolder(
      name: folder['Name'],
      relativeUri: folder['ServerRelativeUrl'],
      itemCount: folder['ItemCount']
    );
  }

  private static SPFile createFile( Object file ) {
    def metadata = file['__metadata'];
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    DecimalFormat nf = new DecimalFormat("#");
    return new SPFile(
      name: file['Name'],
      serverRelativeUrl: file['ServerRelativeUrl'],
      timeCreated: df.parse( file['TimeLastModified'] ),
      timeLastModified: df.parse( file['TimeLastModified'] ),
      length: nf.parse( file['Length'] ),
      checkInComment: file['CheckInComment'],
      checkOutType: file['CheckOutType'],
      contentTag: file['ContentTag'],
      exists: file['Exists'],
      title: file['Title']
    );
  }
  
  private static SPFileVersion createFileVersion( Object file ) {
    def metadata = file['__metadata'];
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    return new SPFileVersion(
      checkInComment: file['CheckInComment'],
      created: df.parse( file['Created'] ),
      id: file['ID'],
      isCurrentVersion: file['IsCurrentVersion'],
      size: file['Size'],
      url: file['Url'],
      versionLabel: file['VersionLabel']
    );
  }
  
  public static SPContextInfo parseContextInfo( String content ) {
    try {
      def json = new JsonSlurper().parseText( content );
      def info = json['d']['GetContextWebInformation']
      return createContextInfo( info );
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
  }
  
  private static SPContextInfo createContextInfo( Object info ) {
    try {
      return new SPContextInfo(
        formDigestTimeoutSeconds: info['FormDigestTimeoutSeconds'],
        formDigestValue: info['FormDigestValue'],
        siteFullUrl: info['SiteFullUrl'],
        webFullUrl: info['WebFullUrl']
      );
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
  }

  
  public static List<SPFile> parseFiles( String content ) throws SpointException {
    
    try {
      def json = new JsonSlurper().parseText( content );
      json['d']['results'].collect { folder ->
        return createFile( folder );
      }
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }
  
  
  public static SPFile parseFile( String content ) throws SpointException {
    
    try {
      def json = new JsonSlurper().parseText( content );
      def file = json['d']
      return createFile( file );
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }
  
  
  public static List<SPFileVersion> parseFileVersions( String content ) throws SpointException {
    
    try {
      def json = new JsonSlurper().parseText( content );
      json['d']['results'].collect { folder ->
      return createFileVersion( folder );
      }
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }
  
  
  public static SPFileVersion parseFileVersion( String content ) throws SpointException {
    
    try {
      def json = new JsonSlurper().parseText( content );
      Object fileVersion = json['d'];
      checkFound( fileVersion );
      return createFileVersion( fileVersion );
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }
  
  
  /**
   * Check if server returned a valid object - otherwise: NOT_FOUND exception
   * 
   * @param object JSON object from server
   * @throws SPException if no value SP object is in the data
   * 
   */
  private static void checkFound( Object object ) throws SpointException {
    if( object['__metadata'] == null ) {
      throw new SpointException( SpointCode.NOT_FOUND, null, null, null );
    }
  }
  
  
  public static SPException parseException( String content ) {
    
    try {
      def json = new JsonSlurper().parseText( content );
      def error = json['error'];
      String codeLine = error['code'];
      int sep = codeLine.indexOf( ',' );
      String code = codeLine.substring( 0, sep ).trim();
      String name = codeLine.substring( sep + 1 ).trim();
      def messageObject = error['message'];
      String lang = messageObject['lang'];
      String message = messageObject['value'];
      return new SPException(
        code: code,
        name: name,
        lang: lang,
        message: message
      );
    }
    catch( JsonException ex ) {
      throw new SpointException( SpointCode.JSON_ERROR, null, ex, null ); 
    }
    
  }

  
}


