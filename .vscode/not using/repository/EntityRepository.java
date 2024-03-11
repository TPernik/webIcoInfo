package cz.madeta.icoJasonParse.repository;

import cz.madeta.icoJasonParse.model.EntityIcodic;

@Repository
public interface EntityRepository extends JpaRepository<EntityIcodic, Long> {
    EntityIcodic findByIcodic(String icodic);
    EntityIcodic findByName(String name);
}