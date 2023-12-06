package com.ajoubooking.demo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBookshelf is a Querydsl query type for Bookshelf
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBookshelf extends EntityPathBase<Bookshelf> {

    private static final long serialVersionUID = -868965807L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBookshelf bookshelf = new QBookshelf("bookshelf");

    public final com.ajoubooking.demo.domain.embed.QColumnAddress columnAddress;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.ajoubooking.demo.domain.embed.QCallNumber startCallNumber;

    public QBookshelf(String variable) {
        this(Bookshelf.class, forVariable(variable), INITS);
    }

    public QBookshelf(Path<? extends Bookshelf> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBookshelf(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBookshelf(PathMetadata metadata, PathInits inits) {
        this(Bookshelf.class, metadata, inits);
    }

    public QBookshelf(Class<? extends Bookshelf> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.columnAddress = inits.isInitialized("columnAddress") ? new com.ajoubooking.demo.domain.embed.QColumnAddress(forProperty("columnAddress")) : null;
        this.startCallNumber = inits.isInitialized("startCallNumber") ? new com.ajoubooking.demo.domain.embed.QCallNumber(forProperty("startCallNumber")) : null;
    }

}

