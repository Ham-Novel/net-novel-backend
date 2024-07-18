package com.ham.netnovel.episode;

import java.util.Optional;

public interface EpisodeService {


    /**
     * episode PK값으로 엔티티를 찾는 메서드, Optional로 반환되므로 사용시 Null체크 필수
     * @param episodeId
     * @return Optional형태로 반환
     */
    Optional<Episode>  getEpisode(Long episodeId);


}
