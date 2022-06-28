package io.twodoku.twodokuserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.twodoku.twodokuserver.models.Board;

public interface BoardInterface extends JpaRepository<Board, Integer> {
}
