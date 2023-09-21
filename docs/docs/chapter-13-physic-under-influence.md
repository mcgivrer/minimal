# Influencer

## Definition

Add new feature in the `PhysicEngine` to define area on the play area where physic and environment can change
the `Entity` behavior.

![Global design approach](https://docs.google.com/drawings/d/e/2PACX-1vQJy--ocFFsoZ7Cpf2ubgdrUmBHITdL90OU4UzqHp5puvzv9Az7Bc-DeLlyrVUfcnqWnwD44_sFZgiE/pub?w=400&h=270)

In this diagram, we can identify the following requirement:

- **E1**: this entity is under the magnet yellow influence only,
- **E2**: the second entity is under both influence water and magnet,
- **E3**: the 3rd entity is not under influence
- **E4**: and the fourth entity is only under the water influencer (blue one).

## Proposed design

![Influencer design proposal](http://www.plantuml.com/plantuml/png/PP2_JiGm38TtFuLvW5iOAtIwuHYGO49YJrFtOYb_giI1GwZlJgBc89JD_Yp__2Md92fATXRkn90ZNAnY3zggbF3H2yidVW4UaAS1RV2NMdlbQC1NYRprIqWi7Fo0RwGbnFjpfUzaWoxxV-a7Js86F8SyMgh045-CzCILstq_XwYfa6TEyd3BEjR96Ax5mwY5O8Kc_QA48YkxQUzL9_AfEdNnLDQ4zOxEaVNsfiRMbl_aPvl9Quf8Lo4_zVy4AxJcS3Z-g0peMWLWUA8-mv6OK3B-3PGKwJ4BjLcxibkudVZ5S2fEsTbl)

## implementation proposal

```java
class Influencer extends Entity {

    //...
}
```

## Impacts

the following impacts has been identified:

| Component/Service | Description                                                                         |
|:------------------|:------------------------------------------------------------------------------------|
| Influencer        | create the component `Influencer` inheriting from the `Entity` class.               |
| PhysicEngine      | add `Influencer` processing, excluding `Influencer` from `Entity` processing        |
| Renderer          | implement a dedicated `DrawPlugin<Influencer>` only used for display debug purposes |


