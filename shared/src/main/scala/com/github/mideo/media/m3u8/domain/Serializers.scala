package com.github.mideo.media.m3u8.domain

import com.github.mideo.media.m3u8._

import scala.collection.mutable

private[m3u8] object Serializers {

  private def reduce(playListPartsString: List[String]): String = {
    playListPartsString.reduce {
      _ + "" + _
    }
  }

  private def stringifyPlaylistMasterPlaylist(masterStreamPlaylist: MasterStreamPlaylist): List[String] = {

    val l = List(masterStreamPlaylist.mediaStreamType.getOrElse(None),
      masterStreamPlaylist.mediaStreamIndependentSegments.getOrElse(None),
      masterStreamPlaylist.mediaStreamTypeInfos.getOrElse(None),
      masterStreamPlaylist.mediaStreamInfo,
      masterStreamPlaylist.mediaStreamFrameInfo)
    stringifyPlaylistPlaylist(l)
  }

  private def stringifyPlaylistVodPlaylist(vodStreamPlaylist: VodStreamPlaylist): List[String] = {

    val l = List(
      vodStreamPlaylist.mediaStreamType.getOrElse(None),
      vodStreamPlaylist.mediaStreamTypeInitializationVectorCompatibilityVersion.getOrElse(None),
      vodStreamPlaylist.mediaStreamTargetDuration.getOrElse(None),
      vodStreamPlaylist.mediaStreamMediaSequence.getOrElse(None),
      vodStreamPlaylist.mediaStreamPlaylistType.getOrElse(None),
      vodStreamPlaylist.mediaStreamProgramDateTime.getOrElse(None),
      vodStreamPlaylist.mediaStreamPlaylistTransportStreams.getOrElse(None),
      vodStreamPlaylist.mediaStreamEnd.getOrElse(None))
    stringifyPlaylistPlaylist(l)
  }

  private def stringifyPlaylistLivePlaylist(liveStreamPlaylist: LiveStreamPlaylist): List[String] = {

    val l = List(
      liveStreamPlaylist.mediaStreamType.getOrElse(None),
      liveStreamPlaylist.mediaStreamTypeInitializationVectorCompatibilityVersion.getOrElse(None),
      liveStreamPlaylist.mediaStreamTargetDuration.getOrElse(None),
      liveStreamPlaylist.mediaStreamMediaSequence.getOrElse(None),
      liveStreamPlaylist.mediaStreamProgramDateTime.getOrElse(None),
      liveStreamPlaylist.mediaStreamPlaylistTransportStreams.getOrElse(None))
    stringifyPlaylistPlaylist(l)
  }

  private def stringifyPlaylistPlaylist(l: List[Any]): List[String] = {
    val arr = mutable.ArrayBuffer.empty[String]
    l foreach {
      case value: Map[_, _] => arr ++= value.values.toList map (_.toString + "\n")
      case value: List[_] => arr ++= value map (_.toString + "\n")
      case x: MediaStreamPlaylistParts => arr += x.toString + "\n"
      case _ => //doNothing
    }
    arr.toList
  }

  implicit class PimpedMediaPlaylist[T <: StreamPlaylist](t: T) {
    def toMasterPlaylistString: String =  (stringifyPlaylistMasterPlaylist _ andThen reduce).apply(t.asInstanceOf[MasterStreamPlaylist])
    def toVodStreamPlaylistString: String = (stringifyPlaylistVodPlaylist _ andThen reduce).apply(t.asInstanceOf[VodStreamPlaylist])
    def toLiveStreamPlaylistString: String = (stringifyPlaylistLivePlaylist _ andThen reduce).apply(t.asInstanceOf[LiveStreamPlaylist])

  }

}