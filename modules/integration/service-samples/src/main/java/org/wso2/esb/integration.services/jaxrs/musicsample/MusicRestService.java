/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.esb.integration.services.jaxrs.musicsample;

import org.wso2.esb.integration.services.jaxrs.musicsample.bean.Music;
import org.wso2.esb.integration.services.jaxrs.musicsample.bean.Singer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/music")
public class MusicRestService {

    @Inject
    private MusicService musicService;

    @GET
    @Path("/albumDetails/musicAlbum")
    @Produces(MediaType.APPLICATION_JSON)
    public Music getAlbumDetailsByName(@QueryParam("album") final String albumName) {
        return musicService.getByAlbum(albumName);
    }

    @POST
    @Path("/albumDetails/custom/addAlbum")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAlbumDetailsReturnCustomMessage(Music music) {

        musicService.setMusic(music);

        String result = "Album Added in POST : " + music;
        return Response.status(Response.Status.CREATED).entity(result).build();

    }

    @POST
    @Path("/albumDetails/addAlbum")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAlbumDetailsReturnJsonObject(Music music) {

        musicService.setMusic(music);
        return Response.status(Response.Status.CREATED).entity(music).build();
    }

    @PUT
    @Path("/albumDetails/updateAlbum")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMusicInJSONPUT(Music music) {

        Music musicOne = musicService.getByAlbum(music.getAlbum());

        musicOne.setSinger(music.getSinger());

        String result = "Album updated form PUT request: " + musicOne;
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @POST
    @Path("singer/addSingerDetails")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSingerDetailsInJSONPOST(Singer singer) {

        musicService.setSinger(singer);

        String result = "Singer Added in POST : " + singer;
        return Response.status(Response.Status.CREATED).entity(result).build();

    }

    @GET
    @Path("singer/getSingerDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public Singer getSingerDetailsByName(@QueryParam("singer") final String singerName) {
        return musicService.getBySinger(singerName);
    }
}